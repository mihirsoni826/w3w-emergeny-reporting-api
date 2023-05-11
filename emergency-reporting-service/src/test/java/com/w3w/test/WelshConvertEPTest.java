package com.w3w.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.w3w.model.ErrorMessage;
import com.w3w.model.ThreeWordAddress;
import com.w3w.model.ThreeWordAddressSuggestions;
import com.w3w.test.utils.TestUtility;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WelshConvertEPTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String welshConvertEP = "http://localhost:8081/emergencyapi/welsh-convert";
    private final String ENGLISH_3wa = "filled.count.soap";
    private final String WELSH_3wa = "ysgafn.dibyn.cefnogaeth";

    @Test
    public void givenEnglish3wa_when3waIsValid_thenReturnValidWelsh3wa() throws Exception {
        ThreeWordAddress expectedResponse = TestUtility.createThreeWordAddress(WELSH_3wa);
        ThreeWordAddress twa = TestUtility.createThreeWordAddress(ENGLISH_3wa);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(twa);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
        ResponseEntity<String> response = restTemplate.exchange(welshConvertEP, HttpMethod.POST, entity, String.class);

        ThreeWordAddress actualResponse = objectMapper.readValue(response.getBody(), ThreeWordAddress.class);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void givenWelsh3wa_when3waIsValid_thenReturnTheSame3wa() throws Exception {
        ThreeWordAddress expectedResponse = TestUtility.createThreeWordAddress(WELSH_3wa);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(expectedResponse);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
        ResponseEntity<String> response = restTemplate.exchange(welshConvertEP, HttpMethod.POST, entity, String.class);

        ThreeWordAddress actualResponse = objectMapper.readValue(response.getBody(), ThreeWordAddress.class);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void givenValidRegexButIncorrectEnglish3wa_whenSuggestionsExist_thenReturnSuggestions() throws Exception {
        ThreeWordAddress twa = TestUtility.createThreeWordAddress("filled.count.so");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(twa);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> response = restTemplate.exchange(welshConvertEP, HttpMethod.POST, entity, String.class);

            ThreeWordAddressSuggestions suggestions = objectMapper.readValue(response.getBody(), ThreeWordAddressSuggestions.class);

            assertEquals(400, response.getStatusCode().value());
            assertEquals("3wa not recognised: filled.count.so", suggestions.getMessage());
            assertEquals(3, suggestions.getSuggestions().size());
        });
    }

    @Test
    public void givenInvalidRegex3wa_whenSuggestionsDontExist_thenReturnBadRequestError() throws Exception {
        ThreeWordAddress twa = TestUtility.createThreeWordAddress("filled.count");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(twa);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> response = restTemplate.exchange(welshConvertEP, HttpMethod.POST, entity, String.class);

            ErrorMessage suggestions = objectMapper.readValue(response.getBody(), ErrorMessage.class);

            assertEquals(400, response.getStatusCode().value());
            assertEquals("3wa address supplied has invalid format", suggestions.getErrorMessage());
        });
    }

    @Test
    public void givenNonNull3wa_when3waIsEmpty_thenReturnBadRequestError() throws Exception {
        ThreeWordAddress twa = TestUtility.createThreeWordAddress("");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(twa);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> response = restTemplate.exchange(welshConvertEP, HttpMethod.POST, entity, String.class);

            ErrorMessage suggestions = objectMapper.readValue(response.getBody(), ErrorMessage.class);

            assertEquals(400, response.getStatusCode().value());
            assertEquals("words or 'null' should be provided", suggestions.getErrorMessage());
        });
    }

    @Test
    public void given3wa_when3waIsNull_thenReturnBadRequestError() throws Exception {
        ThreeWordAddress twa = TestUtility.createThreeWordAddress(null);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestPayload = objectMapper.writeValueAsString(twa);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
        assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> response = restTemplate.exchange(welshConvertEP, HttpMethod.POST, entity, String.class);

            ErrorMessage suggestions = objectMapper.readValue(response.getBody(), ErrorMessage.class);

            assertEquals(400, response.getStatusCode().value());
            assertEquals("Address cannot be null error", suggestions.getErrorMessage());
        });
    }

}
