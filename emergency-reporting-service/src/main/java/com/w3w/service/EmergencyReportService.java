package com.w3w.service;

import com.w3w.exception.*;
import com.w3w.model.EmergencyReport;
import com.w3w.model.FilteredSuggestion;
import com.w3w.model.ThreeWordAddress;
import com.w3w.model.ThreeWordAddressSuggestions;
import com.w3w.utils.Constants;
import com.what3words.javawrapper.What3WordsV3;
import com.what3words.javawrapper.request.Coordinates;
import com.what3words.javawrapper.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmergencyReportService implements IEmergencyReportService {

    private final What3WordsV3 api = new What3WordsV3(Constants.API_KEY);

    @Override
    public ThreeWordAddressSuggestions getAutoSuggestions(EmergencyReport report) {
        List<Suggestion> suggestions;

        if(report.getLatitude() != null && report.getLongitude() != null)
            suggestions = autoSuggestWithFocus(report.getLatitude(), report.getLongitude(), report.getThreeWordAddress());
        else
            suggestions = autoSuggestWithoutFocus(report.getThreeWordAddress());
        
        List<FilteredSuggestion> filteredSuggestions = filterSuggestions(suggestions);

        ThreeWordAddressSuggestions threeWordAddressSuggestions = new ThreeWordAddressSuggestions();
        threeWordAddressSuggestions.setMessage("3wa not recognised: " + report.getThreeWordAddress());
        threeWordAddressSuggestions.setSuggestions(filteredSuggestions);

        return threeWordAddressSuggestions;
    }

    private List<FilteredSuggestion> filterSuggestions(List<Suggestion> suggestions) {
        List<FilteredSuggestion> filteredSuggestions = new ArrayList<>();

        for(Suggestion suggestion : suggestions) {
            FilteredSuggestion tempFilteredSuggestion = new FilteredSuggestion();
            tempFilteredSuggestion.setCountry(suggestion.getCountry());
            tempFilteredSuggestion.setWords(suggestion.getWords());
            tempFilteredSuggestion.setNearestPlace(suggestion.getNearestPlace());

            filteredSuggestions.add(tempFilteredSuggestion);
        }

        return filteredSuggestions;
    }

    private List<Suggestion> autoSuggestWithoutFocus(String threeWordAddress) {
        Autosuggest autosuggest = api.autosuggest(threeWordAddress)
                .clipToCountry(Constants.GREAT_BRITAIN_COUNTRY_CODE)
                .execute();

        if(autosuggest.isSuccessful())
            return autosuggest.getSuggestions();
        else {
            log.error("autoSuggestWithoutFocus - w3w api call failed with error = {}", autosuggest.getError());
            throw new W3WApiException(autosuggest.getError().getKey(), autosuggest.getError().getMessage());
        }
    }

    private List<Suggestion> autoSuggestWithFocus(Double lat, Double lon, String threeWordAddress) {
        Autosuggest autosuggest = api.autosuggest(threeWordAddress)
                                    .clipToCountry(Constants.GREAT_BRITAIN_COUNTRY_CODE)
                                    .focus(new Coordinates(lat, lon))
                                    .execute();

        if(autosuggest.isSuccessful())
            return autosuggest.getSuggestions();
        else {
            log.error("autoSuggestWithFocus - w3w api call failed with error = {}", autosuggest.getError());
            throw new W3WApiException(autosuggest.getError().getKey(), autosuggest.getError().getMessage());
        }
    }

    @Override
    public void convertAddressFormats(EmergencyReport emergencyReport) {
        log.info("convertAddressFormats called to convert addresses into other format");

        Double lat = emergencyReport.getLatitude();
        Double lon = emergencyReport.getLongitude();
        String threeWordAddress = emergencyReport.getThreeWordAddress();

        if((lat == null || lon == null) && threeWordAddress != null) {
            com.what3words.javawrapper.response.Coordinates coordinates = convert3waToCoords(threeWordAddress);
            emergencyReport.setLatitude(coordinates.getLat());
            emergencyReport.setLongitude(coordinates.getLng());

        }
        else if(lat != null && lon != null && threeWordAddress == null) {
            emergencyReport.setThreeWordAddress(convertCoordsTo3wa(lat, lon, Constants.ENGLISH_LANGUAGE_CODE));
        }

        log.info("convertAddressFormats - Addresses converted successfully");
    }

    private String convertCoordsTo3wa(Double lat, Double lon, String lang) {
        log.info("Converting coordinates ({},{}) to three word address", lat, lon);

        ConvertTo3WA words = api.convertTo3wa(new Coordinates(lat, lon))
                .language(lang)
                .execute();

        if(words.isSuccessful())
            return words.getWords();
        else {
            log.error("convertCoordsTo3wa - w3w api call failed with error = {}", words.getError());
            throw new W3WApiException(words.getError().getKey(), words.getError().getMessage());
        }
    }

    private com.what3words.javawrapper.response.Coordinates convert3waToCoords(String threeWordAddress) {
        log.info("Converting three word address {} to coordinates", threeWordAddress);

        ConvertToCoordinates coordinates = api.convertToCoordinates(threeWordAddress)
                .execute();

        if(coordinates.isSuccessful())
            return coordinates.getCoordinates();
        else {
            log.error("convert3waToCoords - w3w api call failed with error = {}", coordinates.getError());
            throw new W3WApiException(coordinates.getError().getKey(), coordinates.getError().getMessage());
        }
    }

    @Override
    public void CreateEmergencyReportPOJOFrom3wa(EmergencyReport emergencyReport, ThreeWordAddress payload) {
        com.what3words.javawrapper.response.Coordinates coordinates = convert3waToCoords(payload.getThreeWordAddress());
        emergencyReport.setThreeWordAddress(payload.getThreeWordAddress());
        emergencyReport.setLatitude(coordinates.getLat());
        emergencyReport.setLongitude(coordinates.getLng());
    }

    @Override
    public String convertEnglishToWelsh(EmergencyReport emergencyReport) {
        return convertCoordsTo3wa(emergencyReport.getLatitude(), emergencyReport.getLongitude(), Constants.WELSH_LANGUAGE_CODE);
    }

    @Override
    public String convertWelshToEnglish(EmergencyReport emergencyReport) {
        return convertCoordsTo3wa(emergencyReport.getLatitude(), emergencyReport.getLongitude(), Constants.ENGLISH_LANGUAGE_CODE);
    }
}
