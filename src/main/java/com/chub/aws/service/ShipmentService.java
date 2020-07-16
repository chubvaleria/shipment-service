package com.chub.aws.service;

import com.chub.aws.model.Shipment;

public interface ShipmentService {

    Shipment save(Shipment shipment);

    void deleteShipment(Shipment shipment);
}
