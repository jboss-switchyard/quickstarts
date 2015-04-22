package org.switchyard.quickstarts.camel.sap.binding;

import org.switchyard.quickstarts.camel.sap.binding.bean.FlightCustomerInfo;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.BookFlightRequest;

public interface FlightCustomerInfoService {
    FlightCustomerInfo getFlightCustomerInfo(BookFlightRequest request);
}
