Shipment-service
================

The shipment-service allows performing standard CRUD operations with shipments using DynamoDB and ElasticSearch. 

Local run
---------
1. Run DynamoDb and ElasticSearch through the ```docker-compose up```
2. ```gradle clean build```
2. ```gradle bootRun``` to run shipment-service application

Note
----
To simplify the usage of API, source files contain Postman collection.


