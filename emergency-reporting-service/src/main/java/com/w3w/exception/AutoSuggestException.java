package com.w3w.exception;

import com.w3w.model.FilteredSuggestion;
import com.what3words.javawrapper.response.Suggestion;
import lombok.Data;

import java.util.List;

@Data
public class AutoSuggestException extends RuntimeException {

    private List<FilteredSuggestion> suggestions;

    public AutoSuggestException(String msg, List<FilteredSuggestion> suggestions) {
        super(msg);
        this.suggestions = suggestions;
    }

}
