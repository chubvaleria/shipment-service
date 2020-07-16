package com.chub.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.chub.aws.entity.ShipmentEntity;
import com.chub.aws.facade.ShipmentFacade;
import com.chub.aws.model.Shipment;
import com.chub.aws.service.impl.DynamoDbService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;

//import com.chub.aws.service.impl.ElasticSearchService;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ShipmentServiceApplication.class)
@WebAppConfiguration
@ActiveProfiles("local")
@TestPropertySource(properties = {
        "amazon.dynamodb.endpoint=http://localhost:8000/",
        "amazon.aws.accesskey=key",
        "amazon.aws.secretkey=key2"})
class ShipmentServiceApplicationTests {

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    DynamoDbService dynamoDbService;

    @Autowired
    ShipmentFacade shipmentFacade;

    // @Autowired
//    ElasticSearchService elasticSearchService;


    @BeforeEach
    public void setup() {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        try {
            CreateTableRequest tableRequest = dynamoDBMapper
                    .generateCreateTableRequest(ShipmentEntity.class);
            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
            CreateTableResult table = amazonDynamoDB.createTable(tableRequest);
            table.getTableDescription();
            //dynamoDBMapper.batchDelete(dynamoDbService.findAll());
        } catch (ResourceInUseException e) {
            System.out.println("Table already created");
        }
    }

    @Test
    public void test() {

        Shipment shipment1 = new Shipment();
        shipment1.setShipmentNumber("shipment#1");
        shipmentFacade.createShipment(shipment1);

        Shipment shipment2 = new Shipment();
        shipment2.setShipmentNumber("shipment#2");
        shipmentFacade.createShipment(shipment2);

        Optional<Shipment> shipmentByShipmentNumber = shipmentFacade.findShipmentByShipmentNumber("shipment#2");
        shipmentFacade.deleteShipment("shipment#2");
        Optional<Shipment> shipmentByShipmentNumber2 = shipmentFacade.findShipmentByShipmentNumber("shipment#2");

        //Shipment save = dynamoDbService.save(shipment2);
        // Shipment save = elasticSearchService.save(shipment1);
        //Shipment save2 = elasticSearchService.save(shipment2);
        // Optional<Shipment> byShipmentNumber = elasticSearchService.findByShipmentNumber("shipment#1");
        System.out.println("passed");
        // dynamoDbService.findById(save.getId().toString());
    }
}
