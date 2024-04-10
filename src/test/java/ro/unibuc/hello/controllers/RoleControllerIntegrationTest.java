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
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ro.unibuc.hello.controllers.contracts.RoleCreateRequest;

import java.util.ArrayList;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Disabled
public class RoleControllerIntegrationTest {

    private MockMvc mockMvc;

    @Container
    private static final MongoDBContainer dbContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4"));
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
    public void testGetRoles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/roles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    public void testCreateRole() throws Exception {
        System.out.println(dbContainer.getReplicaSetUrl());
        RoleCreateRequest roleRequest = new RoleCreateRequest("admin", new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("admin"));

        mockMvc.perform(MockMvcRequestBuilders.post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity: Role already exists"));
    }

    @Test
    public void testGetRoleByName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/roles/admin"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity: Role was not found"));

        RoleCreateRequest roleRequest = new RoleCreateRequest("admin", new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders.post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(roleRequest)));

        mockMvc.perform(MockMvcRequestBuilders.get("/roles/admin"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("admin"));
    }

    @Test
    public void testDeleteRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/roles/admin"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity: Role was not found"));

        RoleCreateRequest roleRequest = new RoleCreateRequest("admin", new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders.post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(roleRequest)));

        mockMvc.perform(MockMvcRequestBuilders.delete("/roles/admin"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/roles/admin"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity: Role was not found"));
    }

    @Test
    public void testUpdateRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/roles/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new RoleCreateRequest("admin", new ArrayList<>())))
        ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity: Role was not found"));

        RoleCreateRequest roleRequest = new RoleCreateRequest("admin", new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders.post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(roleRequest)));

        mockMvc.perform(MockMvcRequestBuilders.put("/roles/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new RoleCreateRequest("operator", new ArrayList<>())))
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("operator"));
    }
}
