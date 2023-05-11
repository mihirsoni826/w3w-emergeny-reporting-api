package com.w3w.test;

import com.w3w.exception.W3WApiException;
import com.w3w.model.EmergencyReport;
import com.w3w.service.EmergencyReportServiceImpl;
import com.w3w.test.utils.TestUtility;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ServiceTest {

    @Test
    public void givenCoordinatesto3wa_whenInvalidApiKey_thenReturnError() {
        EmergencyReportServiceImpl service = new EmergencyReportServiceImpl(TestUtility.INVALID_API_KEY);
        EmergencyReport report = TestUtility.createEmergencyReportObject(TestUtility.w3wLat, TestUtility.w3wLon, null);

        W3WApiException exception = assertThrows(W3WApiException.class, () -> {
            service.convertAddressFormats(report);
        });

        assertEquals("InvalidKey", exception.getErrorCode());
        assertEquals("Authentication failed; invalid API key", exception.getErrorMessage());
    }

    @Test
    public void given3waToCoordinates_whenInvalidApiKey_thenReturnError() {
        EmergencyReportServiceImpl service = new EmergencyReportServiceImpl(TestUtility.INVALID_API_KEY);
        EmergencyReport report = TestUtility.createEmergencyReportObject(null, null, "filled.count.soap");

        W3WApiException exception = assertThrows(W3WApiException.class, () -> {
            service.convertAddressFormats(report);
        });

        assertEquals("InvalidKey", exception.getErrorCode());
        assertEquals("Authentication failed; invalid API key", exception.getErrorMessage());
    }

    @Test
    public void givenIncorrect3wa_whenGetSuggestionsWithInvalidApiKey_thenReturnError() {
        EmergencyReportServiceImpl service = new EmergencyReportServiceImpl(TestUtility.INVALID_API_KEY);
        EmergencyReport report = TestUtility.createEmergencyReportObject(null, null, "filled.count.soap");

        W3WApiException exception = assertThrows(W3WApiException.class, () -> {
            service.getAutoSuggestions(report);
        });

        assertEquals("InvalidKey", exception.getErrorCode());
        assertEquals("Authentication failed; invalid API key", exception.getErrorMessage());
    }

}
