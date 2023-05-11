package com.w3w.test;

import com.w3w.service.EmergencyReportServiceImpl;
import com.w3w.test.utils.TestUtility;
import com.w3w.validation.ServiceValidator;
import org.junit.jupiter.api.Test;
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

}
