package com.chub.aws.facade;

import com.chub.aws.exception.ShipmentNotFoundException;
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
        Shipment shipmentWithId = dynamoDbService.save(shipment);
        return elasticSearchService.save(shipmentWithId);
    }

    @Override
    public Shipment findShipmentById(String uuid) {
        return elasticSearchService.findById(uuid)
                .orElseThrow(() -> new ShipmentNotFoundException(uuid));
    }

    @Override
    @Transactional
    public Shipment updateShipment(Shipment updatedShipment, String uuid) {
        Optional<Shipment> shipmentOptional = elasticSearchService.findById(uuid);
        if (shipmentOptional.isPresent()) {
            Shipment shipment = mapUpdatedFields(updatedShipment, shipmentOptional.get());
            dynamoDbService.save(shipment);
            return elasticSearchService.save(shipment);
        } else {
            throw new ShipmentNotFoundException(uuid);
        }
    }

    @Override
    @Transactional
    public void deleteShipment(String uuid) {
        Optional<Shipment> shipmentOptional = elasticSearchService.findById(uuid);
        if (shipmentOptional.isPresent()) {
            dynamoDbService.deleteShipment(uuid);
            elasticSearchService.deleteShipment(uuid);
        } else {
            throw new ShipmentNotFoundException(uuid);
        }
    }

    private Shipment mapUpdatedFields(Shipment updatedShipment, Shipment shipment) {
        shipment.setShipmentNumber(updatedShipment.getShipmentNumber());
        shipment.setOriginLocation(updatedShipment.getOriginLocation());
        shipment.setDestinationLocation(updatedShipment.getDestinationLocation());
        shipment.setOrders(updatedShipment.getOrders());
        return shipment;
    }
}
