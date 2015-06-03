package org.switchyard.quickstarts.camel.sap.binding;

import org.switchyard.quickstarts.camel.sap.binding.jaxb.BookFlightRequest;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.BookFlightResponse;

public interface FlightBookingService {
    BookFlightResponse bookFlight(BookFlightRequest request);
}
