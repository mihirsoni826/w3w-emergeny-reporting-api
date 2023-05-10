package com.w3w.service;

import com.w3w.model.EmergencyReport;
import com.w3w.model.ThreeWordAddressSuggestions;
import com.what3words.javawrapper.response.Suggestion;

import java.util.List;

public interface IEmergencyReportService {

    ThreeWordAddressSuggestions getAutoSuggestions(EmergencyReport report);

}
