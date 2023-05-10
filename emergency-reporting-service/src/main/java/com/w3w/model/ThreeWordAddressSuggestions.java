package com.w3w.model;

import lombok.Data;

import java.util.List;

@Data
public class ThreeWordAddressSuggestions {

    private String message;
    private List<FilteredSuggestion> suggestions;

}
