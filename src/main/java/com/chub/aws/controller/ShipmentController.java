package com.chub.aws.controller;

import com.chub.aws.exception.ShipmentNotFoundException;
import com.chub.aws.facade.ShipmentFacade;
import com.chub.aws.model.Shipment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentFacade shipmentFacade;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "", consumes = "application/json", produces = "application/json")
    public Shipment createOrUpdateShipment(@RequestBody Shipment shipment) {
        return shipmentFacade.createShipment(shipment);
    }

    @GetMapping(path = "/{uuid}", produces = "application/json")
    public Shipment getShipment(@PathVariable String uuid) {
        return shipmentFacade.findShipmentById(uuid);
    }

    @PutMapping(path = "/{uuid}", produces = "application/json")
    public Shipment updateShipment(@RequestBody Shipment shipment, @PathVariable String uuid) {
        return shipmentFacade.updateShipment(shipment, uuid);

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{uuid}")
    public void deleteShipment(@PathVariable String uuid) {
        shipmentFacade.deleteShipment(uuid);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ShipmentNotFoundException.class)
    public String shipmentNotFoundHandler(ShipmentNotFoundException e) {
        return e.getMessage();
    }
}
