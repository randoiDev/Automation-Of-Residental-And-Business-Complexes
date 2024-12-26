package com.arbc.sports_wellness_center.repositories;

import com.arbc.sports_wellness_center.config.MongoDBClient;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.arbc.sports_wellness_center.models.Constants.DATABASE;
import static com.mongodb.client.model.Filters.eq;

@RequestScoped
public class ReservationsRepository {

    private MongoCollection<Document> reservationAppointmentsCollection;
    @Inject
    private MongoDBClient mongoDBClient;

    @PostConstruct
    public void init() {
        MongoClient mongoClient = mongoDBClient.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase(DATABASE);
        reservationAppointmentsCollection = database.getCollection("reservation_appointments");
    }

    public boolean insertReservationAppointment(Document createReservationAppointment) {
        InsertOneResult result = reservationAppointmentsCollection.insertOne(createReservationAppointment);

        return result.wasAcknowledged();
    }

    public boolean removeReservationAppointment(String reservationAppointmentId) {
        DeleteResult result = reservationAppointmentsCollection.deleteOne(eq("_id",
                new ObjectId(reservationAppointmentId)));

        return result.wasAcknowledged();
    }

    public boolean incrementReservationNumberOnReservationAppointment(String reservationAppointmentId,
                                                                      int version, Document createReservation) {

        // Attempt to update the document with optimistic locking
        Bson filter = Filters.and(
                Filters.eq("_id", new ObjectId(reservationAppointmentId)),
                Filters.eq("version", version) // Check that the version matches for optimistic locking
        );

        // Define the update operations
        Bson updates = Updates.combine(
                Updates.inc("version", 1),  // Increment the version number
                Updates.push("reservations",
                        createReservation)  // Add the new reservation to the reservations array
        );

        // Perform the update operation
        UpdateResult result = reservationAppointmentsCollection.updateOne(filter, updates);

        // Return true if a document was modified, indicating the update was successful
        return result.wasAcknowledged();
    }

    public boolean decrementReservationNumberOnReservationAppointment(
            String reservationAppointmentId, int version, String reservationNumber) {
        Bson filter = Filters.and(
                Filters.eq("_id", new ObjectId(reservationAppointmentId)),
                Filters.eq("version", version) // Check that the version matches for optimistic locking
        );

        // Define the update operation to remove the reservation from the array
        Bson updates = Updates.combine(
                Updates.inc("version", 1),  // Increment the version number
                Updates.pull("reservations",
                        Filters.eq("reservation_number",
                                reservationNumber))  // Remove the reservation with the specified reservation_number
        );

        // Perform the update operation
        UpdateResult result = reservationAppointmentsCollection.updateOne(filter, updates);

        // Return true if a document was modified, indicating the update was successful
        return result.wasAcknowledged();
    }

    public boolean markReservationAsNotArrived(String reservationAppointmentId, String reservationNumber) {

        // Filter the document by its ID
        Bson filter = Filters.eq("_id", new ObjectId(reservationAppointmentId));

        // Define the update operation to set the arrived status to false
        Bson update = Updates.set("reservations.$[reservation].arrived", false);

        // Define the array filter to match the specific reservation by reservation number
        UpdateOptions options = new UpdateOptions().arrayFilters(List.of(
                Filters.eq("reservation.reservation_number", reservationNumber)
        ));

        // Perform the update operation
        UpdateResult result = reservationAppointmentsCollection.updateOne(filter, update, options);

        // Return true if a document was modified
        return result.wasAcknowledged();
    }



    public List<Document> retrieveReservationAppointmentsByResidentsEmail(
            String residentsEmailSubstring, int page, int size) {
        Bson regexFilter = Filters.elemMatch(
                "reservations", Filters.regex("residents_email",
                        residentsEmailSubstring, "i")); // "i" for case insensitive

        // Create a paginated find iterable
        return reservationAppointmentsCollection.find(regexFilter)
                .skip(page * size)
                .limit(size)
                .sort(Sorts.ascending("start_time"))
                .into(new ArrayList<>());
    }

    public List<Document> retrieveReservationAppointmentsByResidentsEmailExact(
            String residentsEmail, int page, int size
    ) {
        return reservationAppointmentsCollection
                .find(eq("reservations.residents_email", residentsEmail))
                .skip(page * size)
                .limit(size)
                .sort(Sorts.ascending("start_time"))
                .into(new ArrayList<>());
    }

    public Document retrieveReservationAppointmentById(String reservationAppointmentId) {

        return reservationAppointmentsCollection
                .find(eq("_id", new ObjectId(reservationAppointmentId)))
                .first();
    }

    public List<Document> retrieveReservationAppointments(int page, int size) {

        return reservationAppointmentsCollection.find()
                .skip(page * size)
                .limit(size)
                .sort(Sorts.ascending("start_time"))
                .into(new ArrayList<>());
    }
}
