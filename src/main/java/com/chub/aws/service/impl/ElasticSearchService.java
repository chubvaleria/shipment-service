package com.chub.aws.service.impl;

import com.chub.aws.document.ShipmentDocument;
import com.chub.aws.model.Shipment;
import com.chub.aws.repository.elastic.ElasticSearchRepository;
import com.chub.aws.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ElasticSearchService implements ShipmentService {

    private final ElasticSearchRepository elasticSearchRepository;

    public Optional<Shipment> findById(String uuid) {
        return elasticSearchRepository.findById(uuid)
                .map(this::toShipment);
    }

    public Shipment save(Shipment shipment) {
        ShipmentDocument shipmentDoc = toShipmentDocument(shipment);
        return toShipment(elasticSearchRepository.save(shipmentDoc));
    }

    public void deleteShipment(String uuid) {
        elasticSearchRepository.deleteById(uuid);
    }

    private Shipment toShipment(ShipmentDocument shipmentDocument) {
        Shipment shipment = new Shipment();
        shipment.setId(UUID.fromString(shipmentDocument.getId()));
        shipment.setShipmentNumber(shipmentDocument.getShipmentNumber());
        shipment.setOriginLocation(shipmentDocument.getOriginLocation());
        shipment.setDestinationLocation(shipmentDocument.getDestinationLocation());
        shipment.setOrders(shipmentDocument.getOrders());

        return shipment;
    }

    private ShipmentDocument toShipmentDocument(Shipment shipment) {
        ShipmentDocument shipmentDocument = new ShipmentDocument();
        shipmentDocument.setId(shipment.getId().toString());
        shipmentDocument.setShipmentNumber(shipment.getShipmentNumber());
        shipmentDocument.setOriginLocation(shipment.getOriginLocation());
        shipmentDocument.setDestinationLocation(shipment.getDestinationLocation());
        shipmentDocument.setOrders(shipment.getOrders());

        return shipmentDocument;
    }
}
