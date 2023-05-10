package com.w3w.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class SuggestionsErrorMessage {

    private String message;
    private ArrayList<ThreeWordAddress> suggestions;

}
