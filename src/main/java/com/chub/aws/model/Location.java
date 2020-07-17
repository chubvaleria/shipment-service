package com.chub.aws.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class Location {

    @NotEmpty(message = "Location name can not be empty")
    private String name;
    private double latitude;
    private double longitude;
    private String timezone;
}
