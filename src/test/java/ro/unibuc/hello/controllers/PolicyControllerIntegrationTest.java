package ro.unibuc.hello.controllers;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class PolicyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    private static MongoDBContainer dbContainer = new MongoDBContainer("mongo:4.4");
    private static MongoClient dbClient;

    private static MongoDatabase db;

    @BeforeAll
    private static void beforeAll() {
        dbContainer.start();
    }

    @AfterEach
    public void afterEach() {
        for(String collectionName : db.listCollectionNames()) {
            db.getCollection(collectionName).deleteMany(new org.bson.Document());
        }
    }

    @AfterAll
    public static void afterAll() {
        db.drop();
        dbClient.close();
        dbContainer.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("mongodb.connection.url", dbContainer::getReplicaSetUrl);
    }




}
