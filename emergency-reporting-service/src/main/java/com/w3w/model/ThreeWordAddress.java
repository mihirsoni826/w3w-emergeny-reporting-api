package com.w3w.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ThreeWordAddress {

    @JsonProperty("3wa")
    private String threeWordAddress;

}
