package com.chub.aws.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
public class Shipment {

    private UUID id;
    private String shipmentNumber;
    private Location originLocation;
    private Location destinationLocation;
    private List<Order> orders = new ArrayList<>();
}
