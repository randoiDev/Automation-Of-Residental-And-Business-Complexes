package com.arbc.residencemanagement.repositories;

import com.arbc.residencemanagement.config.MongoDBClient;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.arbc.residencemanagement.models.Constants.DATABASE;
import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class ResidencesRepository {

    private MongoCollection<Document> residencesCollection;
    @Inject
    private MongoDBClient mongoDBClient;

    @PostConstruct
    public void init() {
        MongoClient mongoClient = mongoDBClient.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase(DATABASE);
        residencesCollection = database.getCollection("residences");
    }

    public List<Document> retrieveResidencesByResidentsEmail(String residentsEmail) {

        return residencesCollection.
                find(eq("residents_email", residentsEmail))
                .projection(Projections.excludeId())
                .into(new ArrayList<>());
    }

    public List<Document> retrieveAvailableResidences() {

        return residencesCollection.
                find(eq("residents_email", null))
                .projection(Projections.excludeId())
                .into(new ArrayList<>());
    }

    public List<Document> retrieveAllResidences() {

        return residencesCollection.find()
                .projection(Projections.excludeId())
                .into(new ArrayList<>());
    }

    public Document retrieveResidenceByResidenceNumber(String residenceNumber) {
        Document query = new Document("residence_number", residenceNumber);

        return residencesCollection.find(query).first();
    }

    public boolean bulkModifyResidencesByResidentsEmail(Set<String> residenceNumbers, String residentsEmail) {
        List<WriteModel<Document>> bulkOperations = new ArrayList<>();

        for (String residenceNumber: residenceNumbers) {
            Bson filter = Filters.eq("residence_number", residenceNumber);
            Bson update = Updates.set("residents_email", residentsEmail);

            WriteModel<Document> updateOne = new UpdateOneModel<>(filter, update);

            bulkOperations.add(updateOne); // Add each update operation to the bulk list
        }

        BulkWriteResult result = residencesCollection.bulkWrite(bulkOperations);

        return result.wasAcknowledged();
    }
}
