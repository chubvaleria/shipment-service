package com.chub.aws.facade;

import com.chub.aws.model.Shipment;
import com.chub.aws.service.impl.DynamoDbService;
import com.chub.aws.service.impl.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentFacadeImpl implements ShipmentFacade {

    private final DynamoDbService dynamoDbService;
    private final ElasticSearchService elasticSearchService;

    @Override
    @Transactional
    public Shipment createShipment(Shipment shipment) {
        shipment.setId(UUID.randomUUID());
        dynamoDbService.save(shipment);
        return elasticSearchService.save(shipment);
    }

    @Override
    public Optional<Shipment> findShipmentByShipmentNumber(String shipmentNumber) {
        return elasticSearchService.findByShipmentNumber(shipmentNumber);
    }

    @Override
    @Transactional
    public Optional<Shipment> updateShipment(Shipment updatedShipment) {
        Optional<Shipment> shipmentOptional = elasticSearchService.findByShipmentNumber(updatedShipment.getShipmentNumber());
        if (shipmentOptional.isPresent()) {
            Shipment shipment = mapUpdatedFields(updatedShipment, shipmentOptional.get());
            dynamoDbService.save(shipment);
            return Optional.of(elasticSearchService.save(shipment));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteShipment(String shipmentNumber) {
        elasticSearchService.findByShipmentNumber(shipmentNumber)
                .ifPresent(shipment -> {
                    dynamoDbService.deleteShipment(shipment);
                    elasticSearchService.deleteShipment(shipment);
                });
    }

    private Shipment mapUpdatedFields(Shipment updatedShipment, Shipment shipment) {
        shipment.setShipmentNumber(updatedShipment.getShipmentNumber());
        shipment.setOriginLocation(updatedShipment.getOriginLocation());
        shipment.setDestinationLocation(updatedShipment.getDestinationLocation());
        shipment.setOrders(updatedShipment.getOrders());
        return shipment;
    }
}
