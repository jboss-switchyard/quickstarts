package org.switchyard.quickstarts.camel.sap.binding;

import org.fusesource.camel.component.sap.model.rfc.Structure;

public interface GetFlightConnectionListService {
    Structure invoke(Structure request);
}
