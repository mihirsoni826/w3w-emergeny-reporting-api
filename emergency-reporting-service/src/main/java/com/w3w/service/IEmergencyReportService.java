package com.w3w.service;

import com.w3w.model.*;

public interface IEmergencyReportService {

    ThreeWordAddressSuggestions getAutoSuggestions(EmergencyReport report);

    void convertAddressFormats(EmergencyReport emergencyReport);
}
