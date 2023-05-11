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
public class EmergencyReportServiceImpl implements IEmergencyReportService {

    private final What3WordsV3 api = new What3WordsV3(Constants.API_KEY);

    @Override
    public ThreeWordAddressSuggestions getAutoSuggestions(EmergencyReport report) {
        List<Suggestion> suggestions = autoSuggestWithoutFocus(report.getThreeWordAddress());
        
        List<FilteredSuggestion> filteredSuggestions = filterSuggestions(suggestions);

        ThreeWordAddressSuggestions threeWordAddressSuggestions = new ThreeWordAddressSuggestions();
        threeWordAddressSuggestions.setMessage("3wa not recognised: " + report.getThreeWordAddress());
        threeWordAddressSuggestions.setSuggestions(filteredSuggestions);

        return threeWordAddressSuggestions;
    }

    /**
     * Method takes in the suggestions as received from w3w api and filters out some fields
     * @param suggestions 3wa suggestions as received from the w3w api
     * @return List of Filtered Suggestions
     * @see FilteredSuggestion
     */
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

    /**
     * This method is used to get suggestions for an incorrect three word address by calling the w3w <b>/autosuggest</b> api.
     * <br>
     * This method passes <b>country code</b> as 'GB' because the emergency services are only based in UK
     * @param threeWordAddress incorrect three word address
     * @return List of suggestions as received from the w3w api
     * @throws W3WApiException if w3w api call is not successful
     * @see W3WApiException
     */
    private List<Suggestion> autoSuggestWithoutFocus(String threeWordAddress) {
        Autosuggest autosuggest = api.autosuggest(threeWordAddress)
                                    .clipToCountry(Constants.GREAT_BRITAIN_COUNTRY_CODE)
                                    .execute();

        if(autosuggest.isSuccessful())
            return autosuggest.getSuggestions();
        else {
            log.error("autoSuggestWithoutFocus - w3w api call failed for 3wa {} with error = {}", threeWordAddress, autosuggest.getError().getMessage());
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
            com.what3words.javawrapper.response.Coordinates coordinates = convert3waToCoords(emergencyReport);
            emergencyReport.setLatitude(coordinates.getLat());
            emergencyReport.setLongitude(coordinates.getLng());

        }
        else if(lat != null && lon != null && threeWordAddress == null) {
            emergencyReport.setThreeWordAddress(convertCoordsTo3wa(lat, lon, Constants.ENGLISH_LANGUAGE_CODE));
        }

        log.info("convertAddressFormats - Addresses converted successfully");
    }

    /**
     * Method to convert coordinates to a valid three word address using the w3w <b>/convert-to-3wa</b> api
     * @param lat Latitude of the address
     * @param lon Longitude of the address
     * @param lang Language in which we want the three word address from the w3w api
     * @return Three word address in the language provided as argument
     * @throws W3WApiException If w3w api fails to convert coordinates to a valid three word address
     * @see W3WApiException
     */
    private String convertCoordsTo3wa(Double lat, Double lon, String lang) {
        log.info("Converting coordinates ({},{}) to three word address", lat, lon);

        ConvertTo3WA words = api.convertTo3wa(new Coordinates(lat, lon))
                                .language(lang)
                                .execute();

        if(words.isSuccessful())
            return words.getWords();
        else {
            log.error("convertCoordsTo3wa - w3w api failed to convert ({},{}) to 3wa with error = {}", lat, lon, words.getError().getMessage());
            throw new W3WApiException(words.getError().getKey(), words.getError().getMessage());
        }
    }

    /**
     * Method to convert a three word address to a set of valid coordinates using the w3w <b>/convert-to-coordinates</b> api
     * <br>
     * The three word address passed to w3w api could still be invalid after the validation check - ///filled.count.soap and ///filled.count.so
     * both look like valid three word addresses as per the regex check. Even though these <i><b>look like</b></i> valid addresses, they are not valid.
     * <br>
     * To handle this edge-case, we check the error code from w3w api response, if error code is 'BadWords', we call the w3w <b>/autosuggest</b> api to get
     * valid suggestions for the incorrect three word address provided.
     * @param emergencyReport EmergencyReport request/response object
     * @return Coordinates of the three word address
     * @throws W3WApiException If w3w api fails to convert coordinates to a valid three word address
     * @see com.w3w.validation.ServiceValidator#is3waValidRegex
     * @see #getAutoSuggestions
     * @see #process3waSuggestionsResponse
     */
    private com.what3words.javawrapper.response.Coordinates convert3waToCoords(EmergencyReport emergencyReport) {
        String threeWordAddress = emergencyReport.getThreeWordAddress();
        log.info("Converting three word address {} to coordinates", threeWordAddress);

        ConvertToCoordinates coordinates = api.convertToCoordinates(threeWordAddress)
                                                .execute();

        if(coordinates.isSuccessful())
            return coordinates.getCoordinates();
        else {
            if(coordinates.getError().getKey().equals(Constants.BAD_WORDS_ERROR_CODE)) {
                ThreeWordAddressSuggestions threeWordAddressSuggestions = getAutoSuggestions(emergencyReport);
                process3waSuggestionsResponse(threeWordAddressSuggestions, threeWordAddress);
            }
            log.error("convert3waToCoords - w3w api failed to convert {} to coordinates with error = {}", threeWordAddress, coordinates.getError().getMessage());
            throw new W3WApiException(coordinates.getError().getKey(), coordinates.getError().getMessage());
        }
    }

    @Override
    public void process3waSuggestionsResponse(ThreeWordAddressSuggestions threeWordAddressSuggestions, String threeWordAddress) {
        if(threeWordAddressSuggestions.getSuggestions() != null && !threeWordAddressSuggestions.getSuggestions().isEmpty()) {
            log.error("validateThreeWordAddress - provided 3wa {} is not recognized, providing suggestions to user = {}",
                    threeWordAddress, threeWordAddressSuggestions.getSuggestions());

            throw new AutoSuggestException(threeWordAddressSuggestions.getMessage(), threeWordAddressSuggestions.getSuggestions());
        }
        else {
            log.error("validateThreeWordAddress - w3w api did not return any suggestions for {}", threeWordAddress);
            throw new BadRequestException("3wa address supplied has invalid format");
        }
    }

    @Override
    public void CreateEmergencyReportPOJOFrom3wa(EmergencyReport emergencyReport, ThreeWordAddress payload) {
        com.what3words.javawrapper.response.Coordinates coordinates = convert3waToCoords(emergencyReport);
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
