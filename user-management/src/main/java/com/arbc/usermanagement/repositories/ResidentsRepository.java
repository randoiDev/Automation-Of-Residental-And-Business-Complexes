package com.arbc.usermanagement.repositories;

import com.arbc.usermanagement.config.MongoDBClient;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.arbc.usermanagement.models.Constants.DATABASE;
import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class ResidentsRepository {

    private MongoCollection<Document> residentsCollection;
    @Inject
    private MongoDBClient mongoDBClient;

    @PostConstruct
    public void init() {
        MongoClient mongoClient = mongoDBClient.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase(DATABASE);
        residentsCollection = database.getCollection("residents");
    }

    public boolean insertResident(Document createResident) {
        InsertOneResult result = residentsCollection.insertOne(createResident);

        return result.wasAcknowledged();
    }

    public boolean modifyResidentPassword(String email, String newPassword) {
        Document query = new Document("email", email);
        Document update = new Document("$set", new Document("password", newPassword));
        UpdateResult result = residentsCollection.updateOne(query, update);

        return result.wasAcknowledged();
    }

    public boolean modifyResidentsBannedForReservationField(String residentId) {
        Optional<Document> residentOpt = Optional.ofNullable(residentsCollection.find(eq("_id",
                new ObjectId(residentId))).first());

        if(residentOpt.isEmpty())
            return false;

        Boolean currentValue = residentOpt.get().getBoolean("banned_for_reservations");

        return residentsCollection.updateOne(eq("_id", new ObjectId(residentId)),
                Updates.set("banned_for_reservations", !currentValue)).wasAcknowledged();
    }

    public boolean removeResident(String residentId) {
        DeleteResult result = residentsCollection.deleteOne(eq("_id", new ObjectId(residentId)));

        return result.wasAcknowledged();
    }

    public Document retrieveResidentByEmail(String email) {
        return residentsCollection
                .find(eq("email", email))
                .projection(Projections.fields(
                        Projections.exclude("password"),
                        Projections.exclude("role")
                ))
                .first();
    }

    public Document retrieveResidentById(String residentId) {
        return residentsCollection.
                find(eq("_id", new ObjectId(residentId)))
                .projection(Projections.fields(
                        Projections.exclude("password"),
                        Projections.exclude("role")))
                .first();
    }

    public Optional<String> retrieveResidentPasswordByEmail(String email) {
        Document password = residentsCollection.find(eq("email", email))
                .projection(Projections.fields(Projections.include("password")))
                .first();

        return Optional.ofNullable(password)
                .map(document -> document.getString("password"));
    }

    public List<Document> retrieveResidentsByEmail(String emailSubstring, int page, int size) {
        Bson regexFilter = Filters.regex("email", emailSubstring, "i"); // "i" for case insensitive

        // Create a paginated find iterable
        return residentsCollection.find(regexFilter)
                .projection(Projections.fields(
                        Projections.exclude("password")))
                .skip(page * size)
                .limit(size)
                .sort(Sorts.ascending("email"))
                .into(new ArrayList<>());
    }
}

