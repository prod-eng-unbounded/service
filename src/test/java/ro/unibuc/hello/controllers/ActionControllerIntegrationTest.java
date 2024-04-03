package ro.unibuc.hello.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.unibuc.hello.controllers.contracts.RoleCreateRequest;
import ro.unibuc.hello.entities.Action;

import java.util.ArrayList;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ActionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    private static MongoDBContainer dbContainer = new MongoDBContainer("mongo:4.4");
    private static MongoClient dbClient;
    private static MongoDatabase db;

    @BeforeAll
    public static void beforeAll() {
        // Start the MongoDB container and create the client and db
        dbContainer.start();

        String connectionString = dbContainer.getReplicaSetUrl();
        dbClient = MongoClients.create(new ConnectionString(connectionString));
        db = dbClient.getDatabase("test");
    }

    @AfterEach
    public void afterEach() {
        // Clear all collections after each test
        for (String collectionName : db.listCollectionNames()) {
            db.getCollection(collectionName).deleteMany(new org.bson.Document());
        }
    }

    @AfterAll
    public static void afterAll() {
        // Drop the database and close the client
        db.drop();
        dbClient.close();
        dbContainer.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // Set the MongoDB connection URL property to the container's url
        registry.add("mongodb.connection.url", dbContainer::getReplicaSetUrl);
    }

    @Test
    public void testGetActions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/actions"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    public void testCreateAction() throws Exception {
        System.out.println(dbContainer.getReplicaSetUrl());
        Action action = new Action("create-tests", "write tests to check app's functionality");

        mockMvc.perform(MockMvcRequestBuilders.post("/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(action)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("create-tests"));

        mockMvc.perform(MockMvcRequestBuilders.post("/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(action)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity: Action already exists"));
    }

    @Test
    public void testGetActionByCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/actions/create-tests"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity: Action was not found"));

        Action action = new Action("create-tests", "write tests to check app's functionality");
        mockMvc.perform(MockMvcRequestBuilders.post("/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(action)));

        mockMvc.perform(MockMvcRequestBuilders.get("/actions/create-tests"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("create-tests"));
    }

    @Test
    public void testDeleteAction() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/actions/create-tests"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity: Action was not found"));

        Action action = new Action("create-tests", "write tests to check app's functionality");
        mockMvc.perform(MockMvcRequestBuilders.post("/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(action)));

        mockMvc.perform(MockMvcRequestBuilders.delete("/actions/create-tests"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/actions/create-tests"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity: Action was not found"));
    }

    @Test
    public void testUpdateAction() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/actions/create-tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new Action("create-tests", "write tests to check app's functionality")))
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity: Action was not found"));

        Action action = new Action("create-tests", "write tests to check app's functionality");
        mockMvc.perform(MockMvcRequestBuilders.post("/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(action)));

        mockMvc.perform(MockMvcRequestBuilders.put("/actions/create-tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new Action("execute-tests", "Execute tests to check app's functionality")))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("execute-tests"));
    }
}
