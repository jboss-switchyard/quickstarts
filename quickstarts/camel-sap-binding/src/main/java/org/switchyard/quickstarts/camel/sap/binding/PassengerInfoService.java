package org.switchyard.quickstarts.camel.sap.binding;

import org.switchyard.quickstarts.camel.sap.binding.bean.PassengerInfo;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.BookFlightRequest;

public interface PassengerInfoService {
    PassengerInfo getPassengerInfo(BookFlightRequest request);
}
