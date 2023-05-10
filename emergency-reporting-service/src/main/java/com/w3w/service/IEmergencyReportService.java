package com.w3w.service;

import com.w3w.exception.AutoSuggestException;
import com.w3w.exception.BadRequestException;
import com.w3w.model.*;

public interface IEmergencyReportService {

    /**
     * Method to return filtered suggestions as received from the w3w api
     * @param report EmergencyReport request/response object
     * @return Filtered 3wa suggestions
     * @see EmergencyReportServiceImpl#autoSuggestWithFocus
     * @see EmergencyReportServiceImpl#autoSuggestWithoutFocus
     * @see EmergencyReportServiceImpl#filterSuggestions
     * @see ThreeWordAddressSuggestions
     */
    ThreeWordAddressSuggestions getAutoSuggestions(EmergencyReport report);

    /**
     * This method is used to convert coordinates to a valid three word address and vice-versa
     * @param emergencyReport EmergencyReport request payload
     * @see EmergencyReportServiceImpl#convertCoordsTo3wa
     * @see EmergencyReportServiceImpl#convert3waToCoords
     */
    void convertAddressFormats(EmergencyReport emergencyReport);

    /**
     * This method calls convert3waToCoords method and sets the coordinates on a EmergencyReport object
     * @param emergencyReport EmergencyReport response object
     * @param payload ThreeWordAddress request payload
     * @see EmergencyReportServiceImpl#convert3waToCoords(EmergencyReport)
     * @see com.w3w.controller.Controller#handleEnglishConvert
     * @see com.w3w.controller.Controller#handleWelshConvert
     */
    void CreateEmergencyReportPOJOFrom3wa(EmergencyReport emergencyReport, ThreeWordAddress payload);

    /**
     * This method calls the convertCoordsTo3wa passing Welsh language code 'cy' to get three word address for the passed coordinates in Welsh
     * @param emergencyReport EmergencyReport object
     * @return Three word address for the passed coordinates in Welsh
     * @see EmergencyReportServiceImpl#convertCoordsTo3wa
     */
    String convertEnglishToWelsh(EmergencyReport emergencyReport);

    /**
     * This method calls the convertCoordsTo3wa passing English language code 'en' to get three word address for the passed coordinates in English
     * @param emergencyReport EmergencyReport object
     * @return Three word address for the passed coordinates in Welsh
     * @see EmergencyReportServiceImpl#convertCoordsTo3wa
     */
    String convertWelshToEnglish(EmergencyReport emergencyReport);

    /**
     * This method is used to check if w3w <b>/autosuggest</b> api returned any response
     * @param threeWordAddressSuggestions filtered suggestions upon receiving from w3w api
     * @param threeWordAddress Three word address as provided in the request payload
     * @throws AutoSuggestException If w3w api returned some valid suggestions for the provided three word address
     * @throws BadRequestException If w3w api did not return any suggestions for the provided three word address
     */
    void process3waSuggestionsResponse(ThreeWordAddressSuggestions threeWordAddressSuggestions, String threeWordAddress);

}
