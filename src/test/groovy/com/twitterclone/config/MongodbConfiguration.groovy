package com.twitterclone.config

import org.springframework.boot.test.context.TestConfiguration
import org.testcontainers.containers.MongoDBContainer

@TestConfiguration
class MongodbConfiguration {

    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0.3")
            .withEnv("MONGO_INITDB_DATABASE", "mydatabase")
//            .withEnv("MONGO_INITDB_ROOT_USERNAME", "testuser")
//            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "testpassword")

    static {
        mongoDBContainer.start()

        System.setProperty("spring.data.mongodb.host", mongoDBContainer.getHost())
        System.setProperty("spring.data.mongodb.port", mongoDBContainer.getFirstMappedPort().toString())
    }
}
