package com.w3w.validation;

import com.w3w.exception.AutoSuggestException;
import com.w3w.exception.BadRequestException;
import com.w3w.model.EmergencyReport;
import com.w3w.model.ThreeWordAddressSuggestions;
import com.w3w.service.IEmergencyReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ServiceValidator {

    private IEmergencyReportService service;

    public ServiceValidator(IEmergencyReportService service) {
        this.service = service;
    }

    public void validateRequestPayload(EmergencyReport report) {
        Double lat = report.getLatitude();
        Double lon = report.getLongitude();
        String w3w = report.getThreeWordAddress();

        if(lat == null && lon == null && w3w == null) {
            log.error("validateRequestPayload - Latitude, Longitude and 3 word address are missing. Please provide either both the coordinates or the 3 word address");
            throw new BadRequestException("Please provide either both the coordinates or the 3 word address");
        }
        if(w3w != null) {
            if(w3w.isEmpty()) {
                log.error("validateRequestPayload - words or 'null' should be provided");
                throw new BadRequestException("words or 'null' should be provided");
            }
            validateThreeWordAddress(report);
        }
        if(lat != null && lon != null) {
            isUKLatAndLong(lat, lon);
        }
        log.info("Request payload validated successfully!");
    }

    private void validateThreeWordAddress(EmergencyReport report) {
        if(is3waValidRegex(report.getThreeWordAddress())) {
            log.info("validateThreeWordAddress - {} is a valid three word address", report.getThreeWordAddress());
        }
        else {
            ThreeWordAddressSuggestions threeWordAddressSuggestions = service.getAutoSuggestions(report);
            service.process3waSuggestionsResponse(threeWordAddressSuggestions, report.getThreeWordAddress());
        }
    }

    private boolean is3waValidRegex(String threeWordAddress) {
        final String regex = "^/*(?:(?:\\p{L}\\p{M}*)+[.｡。･・︒។։။۔።।](?:\\p{L}\\p{M}*)+[.｡。･・︒។։။۔።।](?:\\p{L}\\p{M}*)+|(?:\\p{L}\\p{M}*)+([\u0020\u00A0](?:\\p{L}\\p{M}*)+){1,3}[.｡。･・︒។։။۔።।](?:\\p{L}\\p{M}*)+([\u0020\u00A0](?:\\p{L}\\p{M}*)+){1,3}[.｡。･・︒។։။۔።।](?:\\p{L}\\p{M}*)+([\u0020\u00A0](?:\\p{L}\\p{M}*)+){1,3})$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(threeWordAddress);

        return matcher.find();
    }

    private void isUKLatAndLong(Double lat, Double lon) {
        if(coordinatesInValidRange(lat, lon)) {
            if (lat < 49.8 || lat > 58.7 || lon < -8.6 || lon > 1.8) {
                log.error("isUKLatAndLong - Coordinates are outside UK");
                throw new BadRequestException("Coordinates are outside UK. Please provide UK coordinates");
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

    private boolean coordinatesInValidRange(Double lat, Double lon) {
        return ((lat >= -90 && lat <= 90) && (lon >= -180 && lon <= 180));
    }

}
