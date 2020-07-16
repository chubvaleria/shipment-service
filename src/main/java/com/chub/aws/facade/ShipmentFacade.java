package com.chub.aws.facade;

import com.chub.aws.model.Shipment;

import java.util.Optional;

public interface ShipmentFacade {

    Shipment createShipment(Shipment shipment);

    Optional<Shipment> findShipmentByShipmentNumber(String shipmentNumber);

    Optional<Shipment> updateShipment(Shipment shipment);

    void deleteShipment(String shipmentNumber);
}
