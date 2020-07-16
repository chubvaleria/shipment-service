package com.chub.aws.facade;

import com.chub.aws.model.Shipment;
import com.chub.aws.service.impl.DynamoDbService;
import com.chub.aws.service.impl.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShipmentFacadeImpl implements ShipmentFacade {

    private final DynamoDbService dynamoDbService;
    private final ElasticSearchService elasticSearchService;

    @Override
    @Transactional
    public Shipment createShipment(Shipment shipment) {
        dynamoDbService.save(shipment);
        return elasticSearchService.save(shipment);
    }

    @Override
    public Optional<Shipment> findShipmentByShipmentNumber(String shipmentNumber) {
        return elasticSearchService.findByShipmentNumber(shipmentNumber);
    }

    @Override
    @Transactional
    public Optional<Shipment> updateShipment(Shipment shipment) {
        Optional<Shipment> shipmentOptional = elasticSearchService.findByShipmentNumber(shipment.getShipmentNumber());
        if (shipmentOptional.isPresent()) {
            dynamoDbService.save(shipment);
            return Optional.of(elasticSearchService.save(shipment));
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void deleteShipment(String shipmentNumber) {
        Optional<Shipment> shipmentOptional = elasticSearchService.findByShipmentNumber(shipmentNumber);
        if (shipmentOptional.isPresent()) {
            Shipment shipment = shipmentOptional.get();
            dynamoDbService.deleteShipment(shipment);
            elasticSearchService.deleteShipment(shipment);
        }
    }
}
