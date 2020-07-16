package com.chub.aws.facade;

import com.chub.aws.model.Shipment;

public interface ShipmentFacade {

    Shipment createShipment(Shipment shipment);

    Shipment findShipmentById(String uuid);

    Shipment updateShipment(Shipment shipment, String uuid);

    void deleteShipment(String uuid);
}
