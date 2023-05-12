package com.w3w.test;

import com.w3w.service.EmergencyReportServiceImpl;
import com.w3w.test.utils.TestUtility;
import com.w3w.validation.ServiceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ServiceValidatorTest {

    @Test
    public void givenInvalidRegex_whenPatternSyntaxExceptionThrown_thenReturnError() {
        String invalidRegex = "*\\";
        String threeWordAddress = "filled.count.soap";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ServiceValidator validator = new ServiceValidator(new EmergencyReportServiceImpl(TestUtility.VALID_API_KEY));
            validator.is3waValidRegex(threeWordAddress, invalidRegex);
        });

        assertEquals("Error compiling the regex", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "-91, -181, false",  // Branch 1: Latitude and Longitude both below range
            "-91, 0, false",     // Branch 2: Latitude below range, Longitude within range
            "-91, 181, false",   // Branch 3: Latitude below range, Longitude above range
            "0, -181, false",    // Branch 4: Latitude within range, Longitude below range
            "0, 181, false",     // Branch 5: Latitude within range, Longitude above range
            "91, -181, false",   // Branch 6: Latitude above range, Longitude below range
            "91, 0, false",      // Branch 7: Latitude above range, Longitude within range
            "91, 181, false",    // Branch 8: Latitude and Longitude both above range
            "0, 0, true",        // Test case where both latitude and longitude are within range
            "90, -180, true",    // Test case where both latitude and longitude are at the lower boundary
            "90, 180, true",     // Test case where both latitude and longitude are at the upper boundary
    })
    public void givenSetOfCoordinates_whenValidating_thenReturnAptResponse(Double lat, Double lon, boolean expected) {
        ServiceValidator validator = new ServiceValidator(new EmergencyReportServiceImpl());
        assertEquals(expected, validator.coordinatesInValidRange(lat, lon));
    }

}
