package com.chub.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.chub.aws.facade.ShipmentFacade;
import com.chub.aws.model.Location;
import com.chub.aws.model.Shipment;
import com.chub.aws.repository.dynamodb.DynamoDbShipmentRepository;
import com.chub.aws.repository.elastic.ElasticSearchRepository;
import com.chub.aws.service.impl.DynamoDbService;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Optional;

@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {ShipmentServiceApplicationTests.Initializer.class})
class ShipmentServiceApplicationTests {

    @Container
    public static GenericContainer<?> dynamoDbContainer = new GenericContainer<>("amazon/dynamodb-local:latest")
            .withExposedPorts(8000);

    @Container
    public static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer();

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    DynamoDbService dynamoDbService;

    @Autowired
    ShipmentFacade shipmentFacade;

    @Autowired
    DynamoDbShipmentRepository dynamoDbShipmentRepository;

    @Autowired
    ElasticSearchRepository elasticSearchRepository;

    @BeforeEach
    public void cleanData() {
        dynamoDbShipmentRepository.deleteAll();
        elasticSearchRepository.deleteAll();
    }

    @BeforeClass
    public static void setup() {
       /* dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        try {
            CreateTableRequest tableRequest = dynamoDBMapper
                    .generateCreateTableRequest(ShipmentEntity.class);
            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
            CreateTableResult table = amazonDynamoDB.createTable(tableRequest);
            table.getTableDescription();
            //dynamoDBMapper.batchDelete(dynamoDbService.findAll());
        } catch (ResourceInUseException e) {
            System.out.println("Table already created");
        }*/
    }

    @Test
    public void test() {
        Shipment shipment2 = new Shipment();
        shipment2.setShipmentNumber("shipment#2");
        shipmentFacade.createShipment(shipment2);

        Location location = new Location();
        location.setName("Kharkiv");

        Optional<Shipment> shipmentByShipmentNumber = shipmentFacade.findShipmentByShipmentNumber("shipment#2");
        shipment2.setOriginLocation(location);
        Optional<Shipment> shipment = shipmentFacade.updateShipment(shipment2);
        Optional<Shipment> shipmentByShipmentNumber2 = shipmentFacade.findShipmentByShipmentNumber("shipment#2");

        //Shipment save = dynamoDbService.save(shipment2);
        // Shipment save = elasticSearchService.save(shipment1);
        //Shipment save2 = elasticSearchService.save(shipment2);
        // Optional<Shipment> byShipmentNumber = elasticSearchService.findByShipmentNumber("shipment#1");
        System.out.println("passed");
        // dynamoDbService.findById(save.getId().toString());
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override

        public void initialize(ConfigurableApplicationContext ctx) {
            elasticsearchContainer.start();
            dynamoDbContainer.start();
            TestPropertyValues.of(
                    "amazon.dynamodb.endpoint: " + dynamoDbContainer.getHost() + ":" + dynamoDbContainer.getMappedPort(8000),
                    "amazon.elasticsearch.endpoint: " + elasticsearchContainer.getHttpHostAddress())
                    .applyTo(ctx.getEnvironment());
        }
    }
}
