package com.w3w.service;

import com.w3w.model.*;

public interface IEmergencyReportService {

    ThreeWordAddressSuggestions getAutoSuggestions(EmergencyReport report);
    void convertAddressFormats(EmergencyReport emergencyReport);

    void CreateEmergencyReportPOJOFrom3wa(EmergencyReport emergencyReport, ThreeWordAddress payload);

    String convertEnglishToWelsh(EmergencyReport emergencyReport);

    String convertWelshToEnglish(EmergencyReport emergencyReport);
}
