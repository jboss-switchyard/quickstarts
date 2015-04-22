package org.switchyard.quickstarts.camel.sap.binding;

import java.io.InputStream;

import org.switchyard.annotations.OperationTypes;

public interface FlightBookingPromotedService {
    @OperationTypes(in = "{http://sap.fusesource.org/rfc/nplServer/BOOK_FLIGHT}Request",
                    out = "{http://sap.fusesource.org/rfc/nplServer/BOOK_FLIGHT}Response")
    String bookFlight(InputStream request);
}
