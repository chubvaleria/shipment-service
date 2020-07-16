package com.chub.aws.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {

    private String orderNumber;
    private String orderDescription;
    private BigDecimal totalPrice;
}
