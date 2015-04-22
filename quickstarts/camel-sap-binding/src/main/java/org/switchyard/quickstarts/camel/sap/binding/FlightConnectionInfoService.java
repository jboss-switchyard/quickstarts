package org.switchyard.quickstarts.camel.sap.binding;

import org.switchyard.quickstarts.camel.sap.binding.bean.FlightConnectionInfo;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.BookFlightRequest;

public interface FlightConnectionInfoService {
    FlightConnectionInfo getFlightConnectionInfo(BookFlightRequest request);
}
