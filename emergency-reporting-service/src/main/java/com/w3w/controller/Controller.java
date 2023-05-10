package com.w3w.controller;

import com.w3w.model.EmergencyReport;
import com.w3w.service.*;
import com.w3w.validation.ServiceValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/emergencyapi")
public class Controller {

    private final ServiceValidator validator;

    private final IEmergencyReportService service;

    @Autowired
    public Controller(ServiceValidator validator, IEmergencyReportService service) {
        this.validator = validator;
        this.service = service;
    }

    @PostMapping(value = "/reports", produces = "application/json", consumes = "application/json")
    public ResponseEntity<EmergencyReport> convertAddressFormat(@RequestBody EmergencyReport emergencyReport) {
        log.info("Controller - received request at /reports for payload = {}", emergencyReport);

        validator.validateRequestPayload(emergencyReport);
        log.info("Controller - Request payload validated successfully!");

        service.convertAddressFormats(emergencyReport);

        log.info("Controller - convertAddressFormat ended successfully!");
        return new ResponseEntity<>(emergencyReport, HttpStatus.OK);
    }

}
