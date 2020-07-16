package com.chub.aws.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class ShipmentUtils {

    public String getId(UUID uuid) {
        return Optional.ofNullable(uuid)
                .map(UUID::toString)
                .orElse(UUID.randomUUID().toString());
    }
}
