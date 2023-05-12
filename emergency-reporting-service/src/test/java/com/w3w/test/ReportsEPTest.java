package com.w3w.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.w3w.model.EmergencyReport;
import com.w3w.test.utils.TestUtility;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ReportsEPTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String reportsEP = "http://localhost:8081/emergencyapi/reports";
    private final String w3w3wa = "filled.count.soap";

    @Test
    public void givenCoordinates_whenCoordinatesAreValid_thenReturnAllAddresses() throws Exception {
        EmergencyReport expectedResponse = TestUtility.createEmergencyReportObject(TestUtility.w3wLat, TestUtility.w3wLon, w3w3wa);

        EmergencyReport emergencyReport = TestUtility.createEmergencyReportObject(TestUtility.w3wLat, TestUtility.w3wLon, null);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(emergencyReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
        ResponseEntity<String> response = restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class);

        EmergencyReport actualResponse = objectMapper.readValue(response.getBody(), EmergencyReport.class);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, actualResponse);
    }


    @Test
    public void given3wa_when3waIsValid_thenReturnAllAddresses() throws Exception {
        EmergencyReport expectedResponse = TestUtility.createEmergencyReportObject(TestUtility.w3wLat, TestUtility.w3wLon, w3w3wa);

        EmergencyReport emergencyReport = TestUtility.createEmergencyReportObject(null, null, w3w3wa);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(emergencyReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
        ResponseEntity<String> response = restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class);

        EmergencyReport actualResponse = objectMapper.readValue(response.getBody(), EmergencyReport.class);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void givenAllAddresses_whenAllAreNull_thenReturnBadRequestError() throws Exception {
        EmergencyReport emergencyReport = TestUtility.createEmergencyReportObject(null, null, null);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(emergencyReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class));

        assertTrue(exception.getMessage().contains("Address cannot be null"));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    public void givenGetRequest_whenReportsEndpoint_thenReturnMethodNotAllowedError() throws Exception {
        EmergencyReport emergencyReport = TestUtility.createEmergencyReportObject(TestUtility.w3wLat, TestUtility.w3wLon, null);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(emergencyReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(reportsEP, HttpMethod.GET, entity, String.class));

        assertTrue(exception.getMessage().contains("Request method 'GET' is not supported"));
        assertTrue(exception.getMessage().contains("405"));
    }

    @Test
    public void givenNonNull3wa_when3waIsEmpty_thenReturnBadRequestError() throws Exception {
        EmergencyReport emergencyReport = TestUtility.createEmergencyReportObject(TestUtility.w3wLat, TestUtility.w3wLon, "");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(emergencyReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class));

        assertTrue(exception.getMessage().contains("words or 'null' should be provided"));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    public void givenCoordinates_whenCoordinatesLieOutsideUK_thenReturnBadRequestError() throws Exception {
        EmergencyReport emergencyReport = TestUtility.createEmergencyReportObject(71.520847, TestUtility.w3wLon, null);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(emergencyReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class));

        assertTrue(exception.getMessage().contains("Address is outside UK. Please provide a UK address"));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    public void givenValid3wa_when3waOutsideUK_thenReturnBadRequestError() throws Exception {
        EmergencyReport emergencyReport = TestUtility.createEmergencyReportObject(null, null, "toddler.geologist.animated");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(emergencyReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class));

        assertTrue(exception.getMessage().contains("Address is outside UK. Please provide a UK address"));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    public void givenCoordinates_whenCoordinatesDontExist_thenReturnBadRequestError() throws Exception {
        EmergencyReport emergencyReport = TestUtility.createEmergencyReportObject(711.520847, TestUtility.w3wLon, null);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(emergencyReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class));

        assertTrue(exception.getMessage().contains("Coordinates are invalid. Please provide valid latitude and longitude - latitudes (-90 to 90) and longitudes (-180 to 180)"));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    public void givenValidRegexButIncorrect3wa_whenSuggestionsExist_thenReturnSuggestions() throws Exception {
        EmergencyReport emergencyReport = TestUtility.createEmergencyReportObject(null, null, "filled.count.so");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(emergencyReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class));

        assertTrue(exception.getMessage().contains("3wa not recognised: filled.count.so"));
        assertTrue(exception.getMessage().contains("400"));
        assertEquals(3, exception.getMessage().split("country", -1).length - 1);
    }

    @Test
    public void givenInvalid3wa_whenSuggestionsDontExist_thenReturnBadRequestError() throws Exception {
        EmergencyReport emergencyReport = TestUtility.createEmergencyReportObject(null, null, "filled.");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(emergencyReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class));

        assertTrue(exception.getMessage().contains("3wa address supplied has invalid format"));
        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    public void given3wa_whenRegexIsInvalid_thenReturnBadRequestError() throws Exception {
        EmergencyReport emergencyReport = TestUtility.createEmergencyReportObject(null, null, "filled.count");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(emergencyReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class));

        assertTrue(exception.getMessage().contains("3wa address supplied has invalid format"));
        assertTrue(exception.getMessage().contains("400"));
    }

}
