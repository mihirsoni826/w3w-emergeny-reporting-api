package com.w3w.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.w3w.model.EmergencyReport;
import com.w3w.model.ErrorMessage;
import com.w3w.model.ThreeWordAddressSuggestions;
import com.w3w.test.utils.TestUtility;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class);

            ErrorMessage errorMessage = objectMapper.readValue(responseEntity.getBody(), ErrorMessage.class);
            assertEquals(400, responseEntity.getStatusCode().value());
            assertEquals("Address cannot be null", errorMessage.getErrorMessage());
        });
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

        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(reportsEP, HttpMethod.GET, entity, String.class);

            ErrorMessage errorMessage = objectMapper.readValue(responseEntity.getBody(), ErrorMessage.class);
            assertEquals(400, responseEntity.getStatusCode().value());
            assertEquals("Request method 'GET' is not supported", errorMessage.getErrorMessage());
        });
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

        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class);

            ErrorMessage errorMessage = objectMapper.readValue(responseEntity.getBody(), ErrorMessage.class);
            assertEquals(400, responseEntity.getStatusCode().value());
            assertEquals("words or 'null' should be provided", errorMessage.getErrorMessage());
        });
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

        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class);

            ErrorMessage errorMessage = objectMapper.readValue(responseEntity.getBody(), ErrorMessage.class);
            assertEquals(400, responseEntity.getStatusCode().value());
            assertEquals("Coordinates are outside UK. Please provide UK coordinates", errorMessage.getErrorMessage());
        });
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

        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class);

            ErrorMessage errorMessage = objectMapper.readValue(responseEntity.getBody(), ErrorMessage.class);
            assertEquals(400, responseEntity.getStatusCode().value());
            assertEquals("Coordinates are invalid. Please provide valid latitude and longitude - latitudes (-90 to 90) and longitudes (-180 to 180)", errorMessage.getErrorMessage());
        });
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

        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class);

            ThreeWordAddressSuggestions actualResponse = objectMapper.readValue(responseEntity.getBody(), ThreeWordAddressSuggestions.class);

            assertEquals(400, responseEntity.getStatusCode().value());
            assertEquals("3wa address not recognised: filled.count.so", actualResponse.getMessage());
            assertEquals(3, actualResponse.getSuggestions().size());
        });
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

        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class);

            ErrorMessage actualResponse = objectMapper.readValue(responseEntity.getBody(), ErrorMessage.class);

            assertEquals(400, responseEntity.getStatusCode().value());
            assertEquals("3wa address supplied has invalid format", actualResponse.getErrorMessage());
        });
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

        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(reportsEP, HttpMethod.POST, entity, String.class);

            ErrorMessage actualResponse = objectMapper.readValue(responseEntity.getBody(), ErrorMessage.class);

            assertEquals(400, responseEntity.getStatusCode().value());
            assertEquals("3wa address supplied has invalid format", actualResponse.getErrorMessage());
        });
    }

}
