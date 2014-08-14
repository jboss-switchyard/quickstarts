package org.switchyard.quickstarts.demos.library;

import org.switchyard.quickstarts.demos.library.types.SuggestionRequest;
import org.switchyard.quickstarts.demos.library.types.SuggestionResponse;

public interface SuggestionRules {

    public SuggestionResponse suggestionRequest(SuggestionRequest request);

}
