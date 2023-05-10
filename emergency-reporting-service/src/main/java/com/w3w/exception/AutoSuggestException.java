package com.w3w.exception;

import com.w3w.model.FilteredSuggestion;
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
