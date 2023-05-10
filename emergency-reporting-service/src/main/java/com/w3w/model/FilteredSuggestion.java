package com.w3w.model;

import lombok.Data;

@Data
public class FilteredSuggestion {

    private String country;
    private String nearestPlace;
    private String words;

}
