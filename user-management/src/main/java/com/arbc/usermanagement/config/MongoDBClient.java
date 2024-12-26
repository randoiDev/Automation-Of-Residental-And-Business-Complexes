package com.arbc.usermanagement.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;

import static com.arbc.usermanagement.models.Constants.*;

@Getter
@ApplicationScoped
public class MongoDBClient {

    private MongoClient mongoClient;

    @PostConstruct
    public void init() {

        // Connect to MongoDB database with provided credentials and server URI
        String mongoUri = String.format("mongodb://%s:%s@%s:%s/%s", MONGO_USERNAME,
                MONGO_PASSWORD, MONGO_HOST, MONGO_PORT, DATABASE);
        mongoClient = MongoClients.create(mongoUri);
    }

    @PreDestroy
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}


