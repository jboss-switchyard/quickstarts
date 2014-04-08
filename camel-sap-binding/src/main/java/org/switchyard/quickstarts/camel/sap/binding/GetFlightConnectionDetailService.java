package org.switchyard.quickstarts.camel.sap.binding;

import org.fusesource.camel.component.sap.model.rfc.Structure;

public interface GetFlightConnectionDetailService {
    Structure invoke(Structure request);
}
