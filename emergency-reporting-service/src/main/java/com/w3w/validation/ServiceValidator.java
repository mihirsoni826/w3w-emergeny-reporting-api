package com.w3w.validation;

import com.w3w.exception.BadRequestException;
import com.w3w.model.EmergencyReport;
import com.w3w.model.ThreeWordAddressSuggestions;
import com.w3w.service.IEmergencyReportService;
import com.w3w.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Slf4j
@Component
public class ServiceValidator {

    private final IEmergencyReportService service;

    @Autowired
    public ServiceValidator(IEmergencyReportService service) {
        this.service = service;
    }

    /**
     * Method to validate the request payload received
     * @param report The request payload received as-is
     * @throws BadRequestException if any of the fields in the request payload are invalid
     */
    public void validateRequestPayload(EmergencyReport report) {
        Double lat = report.getLatitude();
        Double lon = report.getLongitude();
        String w3w = report.getThreeWordAddress();

        if(lat == null && lon == null && w3w == null) {
            log.error("validateRequestPayload - Latitude, Longitude and 3 word address are missing. Please provide either both the coordinates or the 3 word address");
            throw new BadRequestException("Address cannot be null");
        }
        if(w3w != null) {
            if(w3w.isEmpty()) {
                log.error("validateRequestPayload - words or 'null' should be provided");
                throw new BadRequestException("words or 'null' should be provided");
            }
            validateThreeWordAddress(report);
            log.info("3wa validated successfully!");
        }
        if(lat != null && lon != null) {
            isUKLatAndLong(lat, lon);
            log.info("Latitude and Longitude validated successfully!");
        }
    }

    /**
     * Method to validate the three word address (3wa) provided in the request payload.
     * If the 3wa does not look like a valid one, it calls getAutoSuggestions() to get suggestions based off of the provided 3wa
     * @param report The request payload received as-is
     * @see IEmergencyReportService#getAutoSuggestions
     * @see IEmergencyReportService#process3waSuggestionsResponse 
     */
    private void validateThreeWordAddress(EmergencyReport report) {
        if(is3waValidRegex(report.getThreeWordAddress(), Constants.VALID_3WA_REGEX)) {
            log.info("validateThreeWordAddress - {} is a valid three word address", report.getThreeWordAddress());
        }
        else {
            // try to find autosuggestions if 3wa does not look like a valid 3wa
            service.getAutoSuggestions(report);
        }
    }

    /**
     * Method to validate if the three word address looks like a valid three word address
     * @param threeWordAddress The three word address provided in the request payload
     * @return true/false based on whether the three word address looks valid
     */
    public boolean is3waValidRegex(String threeWordAddress, String regex) {
        Pattern pattern;
        try {
            pattern = Pattern.compile(regex);
        }
        catch(PatternSyntaxException e) {
            log.error("is3waValidRegex - Error compiling the regex");
            throw new RuntimeException("Error compiling the regex");
        }
        Matcher matcher = pattern.matcher(threeWordAddress);

        return matcher.find();
    }

    /**
     * Method to check if the coordinates provided lie within UK
     * @param lat latitude as provided in the request payload
     * @param lon longitude as provided in the request payload
     * @throws BadRequestException if the coordinates are invalid or if they lie outside UK
     */
    public void isUKLatAndLong(Double lat, Double lon) {
        if(coordinatesInValidRange(lat, lon)) {
            if (lat < 49.8 || lat > 58.7 || lon < -8.6 || lon > 1.8) {
                log.error("isUKLatAndLong - Address is outside UK");
                throw new BadRequestException("Address is outside UK. Please provide a UK address");
            }
            else {
                log.info("isUKLatAndLong - Coordinates are inside UK");
            }
        }
        else {
            log.error("isUKLatAndLong - provided coordinates ({},{}) are invalid", lat, lon);
            throw new BadRequestException("Coordinates are invalid. Please provide valid latitude and longitude - latitudes (-90 to 90) and longitudes (-180 to 180)");
        }
    }

    /**
     * Method to check if the coordinates are valid - latitudes (-90 to 90) and longitudes (-180 to 180)
     * @param lat latitude as provided in the request payload
     * @param lon longitude as provided in the request payload
     * @return true/false is coordinates are valid/invalid respectively
     */
    public boolean coordinatesInValidRange(Double lat, Double lon) {
        return ((lat >= -90 && lat <= 90) && (lon >= -180 && lon <= 180));
    }

}
