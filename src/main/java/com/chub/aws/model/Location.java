package com.chub.aws.model;

import lombok.Data;

@Data
public class Location {

    private String name;
    private double latitude;
    private double longitude;
    private String timezone;
}