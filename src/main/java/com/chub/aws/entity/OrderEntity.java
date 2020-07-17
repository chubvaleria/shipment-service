package com.chub.aws.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Data;

import java.math.BigDecimal;

@Data
@DynamoDBDocument
public class OrderEntity {

    private String orderNumber;
    private String orderDescription;
    private BigDecimal totalPrice;
}
