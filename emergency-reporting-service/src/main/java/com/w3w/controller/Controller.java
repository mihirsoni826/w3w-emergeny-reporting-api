package com.w3w.controller;

import com.w3w.model.EmergencyReport;
import com.w3w.model.ThreeWordAddress;
import com.w3w.service.IEmergencyReportService;
import com.w3w.utils.Constants;
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

    /**
     * Request handler for /reports endpoint
     * @param payload The EmergencyReport request payload with either both the coordinates, the three word address or both
     * @return EmergencyReport response with all addresses
     * @see EmergencyReport
     */
    @PostMapping(value = "/reports", produces = Constants.APPLICATION_JSON, consumes = Constants.APPLICATION_JSON)
    public ResponseEntity<EmergencyReport> convertAddressFormat(@RequestBody EmergencyReport payload) {
        log.info("Controller - received request at /reports for payload = {}", payload);

        validator.validateRequestPayload(payload);

        service.convertAddressFormats(payload);

        log.info("Controller - convertAddressFormat ended successfully!");
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    /**
     * Request handler for /welsh-convert endpoint
     * @param payload The ThreeWordAddress request payload with an English three word address
     * @return ThreeWordAddress response with the three word address translated to Welsh
     * @see ThreeWordAddress
     * @see EmergencyReport
     */
    @PostMapping(value = "/welsh-convert", produces = Constants.APPLICATION_JSON, consumes = Constants.APPLICATION_JSON)
    public ResponseEntity<ThreeWordAddress> handleWelshConvert(@RequestBody ThreeWordAddress payload) {
        log.info("Controller - received request at /welsh-convert for payload = {}", payload);

        EmergencyReport emergencyReport = new EmergencyReport();
        emergencyReport.setThreeWordAddress(payload.getThreeWordAddress());

        validator.validateRequestPayload(emergencyReport);
        service.createEmergencyReportPOJOFrom3wa(emergencyReport, payload);
        validator.isUKLatAndLong(emergencyReport.getLatitude(), emergencyReport.getLongitude());

        String welsh3wa = service.convertEnglishToWelsh(emergencyReport);
        log.info("English Three word address converted to Welsh successfully!");

        payload.setThreeWordAddress(welsh3wa);

        log.info("Controller - /welsh-convert completed successfully!");
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    /**
     * Request handler for /welsh-3wa endpoint
     * @param payload ThreeWordAddress request with Welsh three word address
     * @return ThreeWordAddress response with the Welsh three word address translated to English
     * @see ThreeWordAddress
     * @see EmergencyReport
     */
    @PostMapping(value = "/welsh-3wa", produces = Constants.APPLICATION_JSON, consumes = Constants.APPLICATION_JSON)
    public ResponseEntity<ThreeWordAddress> handleEnglishConvert(@RequestBody ThreeWordAddress payload) {
        log.info("Controller - received request at /welsh-3wa for payload = {}", payload);

        EmergencyReport emergencyReport = new EmergencyReport();
        emergencyReport.setThreeWordAddress(payload.getThreeWordAddress());

        validator.validateRequestPayload(emergencyReport);
        service.createEmergencyReportPOJOFrom3wa(emergencyReport, payload);
        validator.isUKLatAndLong(emergencyReport.getLatitude(), emergencyReport.getLongitude());

        String english3wa = service.convertWelshToEnglish(emergencyReport);
        log.info("Welsh Three word address converted to English successfully!");

        payload.setThreeWordAddress(english3wa);

        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

}
