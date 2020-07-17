package com.chub.aws.repository.elastic;

import com.chub.aws.document.ShipmentDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSearchRepository extends ElasticsearchRepository<ShipmentDocument, String> {

    ShipmentDocument findByShipmentNumber(String shipmentNumber);

    ShipmentDocument deleteByShipmentNumber(String shipmentNumber);
}
