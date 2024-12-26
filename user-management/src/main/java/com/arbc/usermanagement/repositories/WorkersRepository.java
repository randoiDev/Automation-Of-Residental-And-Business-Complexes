package com.arbc.usermanagement.repositories;

import com.arbc.usermanagement.config.MongoDBClient;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
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
public class WorkersRepository {

    private MongoCollection<Document> workersCollection;
    @Inject
    private MongoDBClient mongoDBClient;

    @PostConstruct
    public void init() {
        MongoClient mongoClient = mongoDBClient.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase(DATABASE);
        workersCollection = database.getCollection("workers");
    }

    public boolean insertWorker(Document createWorker) {
        InsertOneResult result = workersCollection.insertOne(createWorker);

        return result.wasAcknowledged();
    }

    public boolean modifyWorkerPassword(String username, String newPassword) {
        Document query = new Document("username", username);
        Document update = new Document("$set", new Document("password", newPassword));
        UpdateResult result = workersCollection.updateOne(query, update);

        return result.wasAcknowledged();
    }

    public boolean modifyWorkerMobileNumber(String username, String newMobileNumber) {
        Document query = new Document("username", username);
        Document update = new Document("$set", new Document("mobile_number", newMobileNumber));
        UpdateResult result = workersCollection.updateOne(query, update);

        return result.wasAcknowledged();
    }

    public boolean removeWorker(String workerId) {
        DeleteResult result = workersCollection.deleteOne(eq("_id", new ObjectId(workerId)));

        return result.wasAcknowledged();
    }

    public Optional<String> retrieveWorkerPasswordByUsername(String username) {
        Document password =  workersCollection
                .find(eq("username", username))
                .projection(Projections.fields(
                        Projections.include("password")
                ))
                .first();

        return Optional.ofNullable(password)
                .map(document -> document.getString("password"));
    }

    public Document retrieveWorkerByUsername(String username) {
        return workersCollection
                .find(eq("username", username))
                .projection(Projections.fields(
                        Projections.exclude("password")
                ))
                .first();
    }

    public Document retrieveWorkerById(String workerId) {
        return workersCollection.
                find(eq("_id", new ObjectId(workerId)))
                .projection(Projections.fields(
                        Projections.exclude("password")))
                .first();
    }

    public List<Document> retrieveWorkersByUsername(String usernameSubstring, int page, int size) {
        Bson regexFilter = Filters.regex("username", usernameSubstring, "i"); // "i" for case insensitive

        // Create a paginated find iterable
        return workersCollection.find(regexFilter)
                .projection(Projections.fields(
                        Projections.exclude("password")))
                .skip(page * size)
                .limit(size)
                .sort(Sorts.ascending("username"))
                .into(new ArrayList<>());
    }
}

