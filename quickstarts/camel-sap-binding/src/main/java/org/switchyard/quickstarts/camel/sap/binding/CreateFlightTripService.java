package org.switchyard.quickstarts.camel.sap.binding;

import org.switchyard.quickstarts.camel.sap.binding.bean.FlightTripRequestInfo;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.BookFlightResponse;

public interface CreateFlightTripService {
    BookFlightResponse invoke(FlightTripRequestInfo request);
}
