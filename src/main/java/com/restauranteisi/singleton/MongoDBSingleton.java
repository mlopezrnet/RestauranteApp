package com.restauranteisi.singleton;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MongoDBSingleton {
    private static MongoDBSingleton instance;
    private MongoDatabase database;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String dbName;

    private MongoDBSingleton() {
    }

    @PostConstruct
    public void init() {
        MongoClient mongoClient = MongoClients.create(mongoUri);
        database = mongoClient.getDatabase(dbName);
    }

    public static MongoDBSingleton getInstance() {
        if (instance == null) {
            synchronized (MongoDBSingleton.class) {
                if (instance == null) {
                    instance = new MongoDBSingleton();
                }
            }
        }
        return instance;
    }
    public MongoDatabase getDatabase() {
        return database;
    }
}
