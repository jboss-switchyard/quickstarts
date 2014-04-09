package org.switchyard.quickstarts.camel.sap.binding;

import org.fusesource.camel.component.sap.model.rfc.Structure;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.BookFlightRequest;

public interface GetFlightConnectionListService {
    Structure invoke(BookFlightRequest request);
}
