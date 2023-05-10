package com.w3w.controller;

import com.w3w.model.EmergencyReport;
import com.w3w.model.ThreeWordAddress;
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
    public ResponseEntity<EmergencyReport> convertAddressFormat(@RequestBody EmergencyReport payload) {
        log.info("Controller - received request at /reports for payload = {}", payload);

        validator.validateRequestPayload(payload);
        log.info("Controller - Request payload validated successfully!");

        service.convertAddressFormats(payload);

        log.info("Controller - convertAddressFormat ended successfully!");
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @PostMapping(value = "/welsh-convert", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ThreeWordAddress> handleWelshConvert(@RequestBody ThreeWordAddress payload) {
        log.info("Controller - received request at /welsh-convert for payload = {}", payload);

        EmergencyReport emergencyReport = new EmergencyReport();
        emergencyReport.setThreeWordAddress(payload.getThreeWordAddress());

        validator.validateRequestPayload(emergencyReport);
        service.CreateEmergencyReportPOJOFrom3wa(emergencyReport, payload);
        log.info("Controller - Request payload validated successfully!");

        String welsh3wa = service.convertEnglishToWelsh(emergencyReport);
        payload.setThreeWordAddress(welsh3wa);

        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

}
