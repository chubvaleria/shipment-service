package com.chub.aws.document;

import com.chub.aws.model.Location;
import com.chub.aws.model.Order;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(indexName = "shipments")
public class ShipmentDocument {

    private String id;

    private String shipmentNumber;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Location originLocation;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Location destinationLocation;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Order> orders = new ArrayList<>();
}
