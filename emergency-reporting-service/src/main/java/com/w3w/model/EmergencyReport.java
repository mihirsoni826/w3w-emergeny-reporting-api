package com.w3w.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmergencyReport {

    @JsonProperty("message")
    String message;

    @JsonProperty("lat")
    private Double latitude;

    @JsonProperty("lng")
    private Double longitude;

    @JsonProperty("3wa")
    private String threeWordAddress;

    @JsonProperty("reportingOfficerName")
    private String reportingOfficerName;

}
