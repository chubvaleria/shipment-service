package com.chub.aws.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
public class Order {

    @NotEmpty(message = "Order number can not be empty")
    private String orderNumber;
    private String orderDescription;
    private BigDecimal totalPrice;
}
