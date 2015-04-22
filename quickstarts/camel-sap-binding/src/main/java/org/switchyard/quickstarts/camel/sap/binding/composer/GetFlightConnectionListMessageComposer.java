/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.quickstarts.camel.sap.binding.composer;

import java.util.Date;

import org.fusesource.camel.component.sap.SapSynchronousRfcDestinationEndpoint;
import org.fusesource.camel.component.sap.model.rfc.Structure;
import org.fusesource.camel.component.sap.model.rfc.Table;
import org.fusesource.camel.component.sap.util.RfcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.Exchange;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelMessageComposer;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.BookFlightRequest;

/**
 * A MessageComposer for GetFlightConnectionListService.
 */
public class GetFlightConnectionListMessageComposer extends CamelMessageComposer {
    private static final Logger LOG = LoggerFactory.getLogger(GetFlightConnectionListMessageComposer.class);
    
    @Override
    public CamelBindingData decompose(Exchange exchange, CamelBindingData target) throws Exception {
        CamelBindingData response = super.decompose(exchange, target);
        
        // Get BOOK_FLIGHT Request JAXB Bean object.
        BookFlightRequest bookFlightRequest = exchange.getMessage().getContent(BookFlightRequest.class);

        // Create SAP Request object from target endpoint.
        SapSynchronousRfcDestinationEndpoint endpoint = response.getMessage().getExchange().getContext().getEndpoint("sap-srfc-destination:nplDest:BAPI_FLCONN_GETLIST", SapSynchronousRfcDestinationEndpoint.class);
        Structure request = endpoint.createRequest();

        // Add Travel Agency Number to request if set
        if (bookFlightRequest.getTravelAgencyNumber() != null && bookFlightRequest.getTravelAgencyNumber().length() > 0) {
            request.put("TRAVELAGENCY", bookFlightRequest.getTravelAgencyNumber());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added TRAVELAGENCY = '{}' to request", bookFlightRequest.getTravelAgencyNumber());
            }
        } else {
            throw new Exception("No Travel Agency Number");
        }

        // Add Flight Date to request if set
        if (bookFlightRequest.getFlightDate() != null) {
            @SuppressWarnings("unchecked")
            Table<Structure> table = request.get("DATE_RANGE", Table.class);
            Structure date_range = table.add();
            date_range.put("SIGN", "I");
            date_range.put("OPTION", "EQ");
            Date date = bookFlightRequest.getFlightDate();
            date_range.put("LOW", date);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added DATE_RANGE = '{}' to request", RfcUtil.marshal(table));
            }
        } else {
            throw new Exception("No Flight Date");
        }

        // Add Start Destination if set
        if (bookFlightRequest.getStartAirportCode() != null && bookFlightRequest.getStartAirportCode().length() > 0) {
            Structure destination_from = request.get("DESTINATION_FROM", Structure.class);
            destination_from.put("AIRPORTID", bookFlightRequest.getStartAirportCode());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added DESTINATION_FROM = '{}' to request", RfcUtil.marshal(destination_from));
            }
        } else {
            throw new Exception("No Start Destination");
        }

        // Add End Destination if set
        if (bookFlightRequest.getEndAirportCode() != null && bookFlightRequest.getEndAirportCode().length() > 0) {
            Structure destination_to = request.get("DESTINATION_TO", Structure.class);
            destination_to.put("AIRPORTID", bookFlightRequest.getEndAirportCode());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added DESTINATION_TO = '{}' to request", RfcUtil.marshal(destination_to));
            }
        } else {
            throw new Exception("No End Destination");
        }

        // Put request object into body of exchange message.
        response.getMessage().setBody(request);
        return response;
    }
}
