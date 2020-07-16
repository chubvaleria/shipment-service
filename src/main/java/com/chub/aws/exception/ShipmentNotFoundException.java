package com.chub.aws.exception;

public class ShipmentNotFoundException extends RuntimeException {

    public ShipmentNotFoundException(String id) {
        super("Could not find shipment " + id);
    }
}
