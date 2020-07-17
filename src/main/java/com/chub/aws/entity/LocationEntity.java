package com.chub.aws.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Data;

@Data
@DynamoDBDocument
public class LocationEntity {

    private String name;
    private double latitude;
    private double longitude;
    private String timezone;
}
