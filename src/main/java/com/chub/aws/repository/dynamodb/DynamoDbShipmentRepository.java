package com.chub.aws.repository.dynamodb;

import com.chub.aws.entity.ShipmentEntity;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface DynamoDbShipmentRepository extends CrudRepository<ShipmentEntity, String> {

}
