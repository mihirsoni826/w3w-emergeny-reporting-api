package com.w3w.controller;

import com.w3w.model.EmergencyReport;
import com.w3w.model.ThreeWordAddress;
import com.w3w.service.IEmergencyReportService;
import com.w3w.validation.ServiceValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
        log.info("English Three word address converted to Welsh successfully!");

        payload.setThreeWordAddress(welsh3wa);

        log.info("Controller - /welsh-convert completed successfully!");
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @PostMapping(value = "/welsh-3wa", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ThreeWordAddress> handleEnglishConvert(@RequestBody ThreeWordAddress payload) {
        log.info("Controller - received request at /welsh-3wa for payload = {}", payload);

        EmergencyReport emergencyReport = new EmergencyReport();
        emergencyReport.setThreeWordAddress(payload.getThreeWordAddress());

        validator.validateRequestPayload(emergencyReport);
        service.CreateEmergencyReportPOJOFrom3wa(emergencyReport, payload);
        log.info("Controller - Request payload validated successfully!");

        String english3wa = service.convertWelshToEnglish(emergencyReport);
        log.info("Welsh Three word address converted to English successfully!");

        payload.setThreeWordAddress(english3wa);

        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

}
