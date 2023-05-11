package com.w3w.test.utils;

import com.w3w.model.EmergencyReport;
import com.w3w.model.ThreeWordAddress;

public class TestUtility {

    public static final String VALID_API_KEY = "318QA8P4";
    public static final String INVALID_API_KEY = "INVALID_API_KEY";
    public static final Double w3wLat = 51.520847;
    public static final Double w3wLon = -0.195521;

    public static ThreeWordAddress createThreeWordAddress(String threeWordAddress) {
        ThreeWordAddress twa = new ThreeWordAddress();
        twa.setThreeWordAddress(threeWordAddress);
        return twa;
    }

    public static EmergencyReport createEmergencyReportObject(Double lat, Double lon, String threeWordAddress) {
        EmergencyReport report = new EmergencyReport();
        report.setLatitude(lat);
        report.setLongitude(lon);
        report.setThreeWordAddress(threeWordAddress);
        report.setMessage("Test message");
        report.setReportingOfficerName("John Doe");

        return report;
    }

}
