package com.chub.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.chub.aws.entity.ShipmentEntity;
import com.chub.aws.facade.ShipmentFacade;
import com.chub.aws.model.Location;
import com.chub.aws.model.Shipment;
import com.chub.aws.repository.dynamodb.DynamoDbShipmentRepository;
import com.chub.aws.repository.elastic.ElasticSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@ActiveProfiles("it")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {ShipmentServiceApplicationTests.Initializer.class})
class ShipmentServiceApplicationTests {

    @Container
    public static GenericContainer<?> dynamoDbContainer = new GenericContainer<>("amazon/dynamodb-local:latest")
            .withExposedPorts(8000);

    @Container
    public static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.8.0");

    @Autowired
    AmazonDynamoDB amazonDynamoDB;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ShipmentFacade shipmentFacade;
    @Autowired
    DynamoDbShipmentRepository dynamoDbShipmentRepository;
    @Autowired
    ElasticSearchRepository elasticSearchRepository;

    @BeforeEach
    public void setup() {
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        try {
            CreateTableRequest tableRequest = dynamoDBMapper
                    .generateCreateTableRequest(ShipmentEntity.class);
            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
            amazonDynamoDB.createTable(tableRequest);
            cleanUp();
        } catch (ResourceInUseException e) {
            System.out.println("Table already created");
        }
    }

    @Test
    void shouldCreateShipment_whenValidData() throws Exception {
        Shipment shipment = createTestShipment();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(shipment)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotCreateShipment_whenInvalidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new Shipment())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldGet_whenShipmentExists() throws Exception {
        Shipment shipment = createTestShipment();
        Shipment shipmentWithId = shipmentFacade.createShipment(shipment);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/shipments/{uuid}", shipmentWithId.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn404_whenShipmentDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/shipments/{uuid}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdate_whenShipmentExists() throws Exception {
        Shipment shipment = createTestShipment();
        Shipment shipmentWithId = shipmentFacade.createShipment(shipment);

        Location destinationLocation = new Location();
        destinationLocation.setName("Kyiv");
        shipmentWithId.setDestinationLocation(destinationLocation);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/shipments/{uuid}", shipmentWithId.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(shipmentWithId))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.destinationLocation.name").value("Kyiv"));
    }

    @Test
    public void shouldDelete_whenShipmentExists() throws Exception {
        Shipment shipment = createTestShipment();
        Shipment shipmentWithId = shipmentFacade.createShipment(shipment);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/shipments/{uuid}", shipmentWithId.getId()))
                .andExpect(status().isNoContent());
    }

    private void cleanUp() {
        dynamoDbShipmentRepository.deleteAll();
        elasticSearchRepository.deleteAll();
    }


    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Shipment createTestShipment() {
        Shipment shipment = new Shipment();
        shipment.setShipmentNumber("shipment#1");

        Location location = new Location();
        location.setName("Kharkiv");
        shipment.setOriginLocation(location);

        return shipment;
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override

        public void initialize(ConfigurableApplicationContext ctx) {
            TestPropertyValues.of(
                    "amazon.dynamodb.endpoint: http://localhost:" + dynamoDbContainer.getMappedPort(8000),
                    "amazon.elasticsearch.endpoint: " + elasticsearchContainer.getHttpHostAddress())
                    .applyTo(ctx.getEnvironment());
        }
    }
}
