package com.chub.aws.service.impl;

import com.chub.aws.entity.LocationEntity;
import com.chub.aws.entity.OrderEntity;
import com.chub.aws.entity.ShipmentEntity;
import com.chub.aws.model.Location;
import com.chub.aws.model.Order;
import com.chub.aws.model.Shipment;
import com.chub.aws.repository.dynamodb.DynamoDbShipmentRepository;
import com.chub.aws.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.chub.aws.util.ShipmentUtils.getId;

@Service
@RequiredArgsConstructor
public class DynamoDbService implements ShipmentService {

    private final DynamoDbShipmentRepository dynamoDbShipmentRepository;

    public Shipment save(Shipment shipment) {
        ShipmentEntity shipmentEntity = dynamoDbShipmentRepository.save(toShipmentEntity(shipment));
        return toShipment(shipmentEntity);
    }

    public void deleteShipment(Shipment shipment) {
        dynamoDbShipmentRepository.delete(toShipmentEntity(shipment));
    }

    private ShipmentEntity toShipmentEntity(Shipment shipment) {
        ShipmentEntity shipmentEntity = new ShipmentEntity();
        shipmentEntity.setId(getId(shipment.getId()));
        shipmentEntity.setShipmentNumber(shipment.getShipmentNumber());
        shipmentEntity.setOriginLocationEntity(toLocationEntity(shipment.getOriginLocation()));
        shipmentEntity.setDestinationLocationEntity(toLocationEntity(shipment.getDestinationLocation()));
        shipmentEntity.setOrderEntities(toOrderEntities(shipment.getOrders()));

        return shipmentEntity;
    }

    private Shipment toShipment(ShipmentEntity shipmentEntity) {
        Shipment shipment = new Shipment();
        shipment.setId(UUID.fromString(shipmentEntity.getId()));
        shipment.setShipmentNumber(shipmentEntity.getShipmentNumber());
        shipment.setOriginLocation(toLocation(shipmentEntity.getOriginLocationEntity()));
        shipment.setDestinationLocation(toLocation(shipmentEntity.getDestinationLocationEntity()));
        shipment.setOrders(toOrders(shipmentEntity.getOrderEntities()));

        return shipment;
    }

    private LocationEntity toLocationEntity(Location location) {
        if (location != null) {
            LocationEntity locationEntity = new LocationEntity();
            locationEntity.setName(location.getName());
            locationEntity.setLatitude(location.getLatitude());
            locationEntity.setLongitude(location.getLongitude());
            locationEntity.setTimezone(location.getTimezone());

            return locationEntity;
        }
        return null;
    }

    private Location toLocation(LocationEntity locationEntity) {
        if (locationEntity != null) {
            Location location = new Location();
            location.setName(locationEntity.getName());
            location.setLatitude(locationEntity.getLatitude());
            location.setLongitude(locationEntity.getLongitude());
            location.setTimezone(locationEntity.getTimezone());

            return location;
        }
        return null;
    }

    private List<OrderEntity> toOrderEntities(List<Order> orders) {
        return orders.stream()
                .map(this::toOrderEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Order> toOrders(List<OrderEntity> orderEntities) {
        return orderEntities.stream()
                .map(this::toOrder)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private OrderEntity toOrderEntity(Order order) {
        if (order != null) {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setOrderNumber(order.getOrderNumber());
            orderEntity.setOrderDescription(order.getOrderDescription());
            orderEntity.setTotalPrice(order.getTotalPrice());

            return orderEntity;
        }
        return null;
    }

    private Order toOrder(OrderEntity orderEntity) {
        if (orderEntity != null) {
            Order order = new Order();
            order.setOrderNumber(orderEntity.getOrderNumber());
            order.setOrderDescription(orderEntity.getOrderDescription());
            order.setTotalPrice(orderEntity.getTotalPrice());

            return order;
        }
        return null;
    }
}
