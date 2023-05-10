package com.w3w.validation;

import com.w3w.exception.BadRequestException;
import com.w3w.model.EmergencyReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ServiceValidator {

    public void validateRequestPayload(EmergencyReport report) {
        validateCombinationOfLatLong3wa(report);
    }

    private void validateCombinationOfLatLong3wa(EmergencyReport report) {
        Double lat = report.getLatitude();
        Double lon = report.getLongitude();
        String w3w = report.getThreeWordAddress();

        if(lat == null && lon == null && w3w == null) {
            log.error("validateCombinationOfLatLong3wa - Latitude, Longitude and 3 word address are missing. Please provide either both the coordinates or the 3 work address");
            throw new BadRequestException("Please provide either both the coordinates or the 3 work address");
        }
        if(w3w != null && !w3w.isEmpty()) {
            validateThreeWordAddress(w3w);
        }
        if(lat != null && lon != null) {
            isUKLatAndLong(lat, lon);
        }

    }

    private void validateThreeWordAddress(String w3w) {
        String regex = "^/*(?:(?:\\p{L}\\p{M}*)+[.｡。･・︒។։။۔።।](?:\\p{L}\\p{M}*)+[.｡。･・︒។։။۔።।](?:\\p{L}\\p{M}*)+|(?:\\p{L}\\p{M}*)+([\u0020\u00A0](?:\\p{L}\\p{M}*)+){1,3}[.｡。･・︒។։။۔።।](?:\\p{L}\\p{M}*)+([\u0020\u00A0](?:\\p{L}\\p{M}*)+){1,3}[.｡。･・︒។։။۔።।](?:\\p{L}\\p{M}*)+([\u0020\u00A0](?:\\p{L}\\p{M}*)+){1,3})$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(w3w);

        if (matcher.find()) {
            log.info("validateThreeWordAddress - {} is a valid three word address", w3w);
        }
        else {
            log.error("validateThreeWordAddress - {} is not a valid three word address", w3w);
            throw new BadRequestException("3wa address supplied has invalid format");
        }
    }

    private void isUKLatAndLong(Double lat, Double lon) {

        if (lat < 49.8 || lat > 58.7 || lon < -8.6 || lon > 1.8) {
            log.error("isUKLatAndLong - Coordinates are outside UK");
            throw new BadRequestException("Coordinates are outside UK. Please provide UK coordinates");
        }
        else {
            log.info("isUKLatAndLong - Coordinates are inside UK");
        }
    }

}
