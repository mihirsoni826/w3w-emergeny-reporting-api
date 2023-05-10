package com.w3w.service;

import com.w3w.exception.BadRequestException;
import com.w3w.model.EmergencyReport;
import com.w3w.model.FilteredSuggestion;
import com.w3w.model.ThreeWordAddressSuggestions;
import com.what3words.javawrapper.What3WordsV3;
import com.what3words.javawrapper.request.Coordinates;
import com.what3words.javawrapper.response.Autosuggest;
import com.what3words.javawrapper.response.Suggestion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmergencyReportService implements IEmergencyReportService {

    private final String API_KEY = "318QA8P4";
    private final What3WordsV3 api = new What3WordsV3(API_KEY);
    private final String GB = "GB";

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
                .clipToCountry(GB)
                .execute();

        if(autosuggest.isSuccessful())
            return autosuggest.getSuggestions();
        else {
            log.error("autoSuggestWithoutFocus - w3w api call failed with error = {}", autosuggest.getError());
            throw new BadRequestException("3wa address supplied has invalid format");
        }
    }

    private List<Suggestion> autoSuggestWithFocus(Double lat, Double lon, String threeWordAddress) {
        Autosuggest autosuggest = api.autosuggest(threeWordAddress)
                                    .clipToCountry(GB)
                                    .focus(new Coordinates(lat, lon))
                                    .execute();

        if(autosuggest.isSuccessful())
            return autosuggest.getSuggestions();
        else {
            log.error("autoSuggestWithFocus - w3w api call failed with error = {}", autosuggest.getError());
            throw new BadRequestException("3wa address supplied has invalid format");
        }
    }


}
