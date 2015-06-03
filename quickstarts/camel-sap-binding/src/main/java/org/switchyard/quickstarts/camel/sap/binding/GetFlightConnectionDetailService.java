package org.switchyard.quickstarts.camel.sap.binding;

import org.fusesource.camel.component.sap.model.rfc.Structure;
import org.switchyard.quickstarts.camel.sap.binding.bean.FlightConnectionInfo;

public interface GetFlightConnectionDetailService {
    FlightConnectionInfo invoke(Structure request);
}
