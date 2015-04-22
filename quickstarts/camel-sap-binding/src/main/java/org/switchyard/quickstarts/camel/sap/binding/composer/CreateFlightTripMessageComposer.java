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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.activation.DataHandler;

import org.fusesource.camel.component.sap.SapSynchronousRfcDestinationEndpoint;
import org.fusesource.camel.component.sap.model.rfc.Structure;
import org.fusesource.camel.component.sap.model.rfc.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelMessageComposer;
import org.switchyard.quickstarts.camel.sap.binding.bean.FlightConnectionInfo;
import org.switchyard.quickstarts.camel.sap.binding.bean.FlightCustomerInfo;
import org.switchyard.quickstarts.camel.sap.binding.bean.FlightHop;
import org.switchyard.quickstarts.camel.sap.binding.bean.FlightTripRequestInfo;
import org.switchyard.quickstarts.camel.sap.binding.bean.PassengerInfo;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.BookFlightResponse;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.ConnectionInfo;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.ConnectionInfoTable;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.FlightInfo;

/**
 * A MessageComposer for CreateFlightTripService.
 */
public class CreateFlightTripMessageComposer extends CamelMessageComposer {
    private static final Logger LOG = LoggerFactory.getLogger(CreateFlightTripMessageComposer.class);
    private static final String FLIGHT_TRIP_REQUEST_INFO = "flightTripRequestInfo";

    @Override
    public CamelBindingData decompose(Exchange exchange, CamelBindingData target) throws Exception {
        CamelBindingData response = super.decompose(exchange, target);

        // Get flight connection info, flight customer info and passenger info bean objects
        FlightTripRequestInfo flightTripRequestInfo = exchange.getMessage().getContent(FlightTripRequestInfo.class);
        FlightConnectionInfo flightConnectionInfo = flightTripRequestInfo.getFlightConnectionInfo();
        FlightCustomerInfo flightCustomerInfo = flightTripRequestInfo.getFlightCustomerInfo();
        PassengerInfo passengerInfo = flightTripRequestInfo.getPassengerInfo();
        exchange.getContext().setProperty(FLIGHT_TRIP_REQUEST_INFO, flightTripRequestInfo, Scope.EXCHANGE);

        // Create SAP Request object from target endpoint.
        SapSynchronousRfcDestinationEndpoint endpoint = response.getMessage().getExchange().getContext().getEndpoint("sap-srfc-destination:nplDest:BAPI_FLTRIP_CREATE", SapSynchronousRfcDestinationEndpoint.class);
        Structure request = endpoint.createRequest();

        //
        // Add Flight Trip Data to request object.
        //

        Structure flightTripData = request.get("FLIGHT_TRIP_DATA", Structure.class);

        // Add Travel Agency Number to request if set
        String travelAgencyNumber = flightConnectionInfo.getTravelAgencyNumber();
        if (travelAgencyNumber != null && travelAgencyNumber.length() != 0) {
            flightTripData.put("AGENCYNUM", travelAgencyNumber);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added AGENCYNUM = '{}' to FLIGHT_TRIP_DATA", travelAgencyNumber);
            }

        }

        // Add Customer ID to request if set
        String flightCustomerNumber = flightCustomerInfo.getCustomerNumber();
        if (flightCustomerNumber != null && flightCustomerNumber.length() != 0) {
            flightTripData.put("CUSTOMERID", flightCustomerNumber);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added CUSTOMERID = '{}' to FLIGHT_TRIP_DATA", flightCustomerNumber);
            }

        }

        // Add Flight Connection Number to request if set
        String flightConnectionNumber = flightConnectionInfo.getFlightConnectionNumber();
        if (flightConnectionNumber != null && flightConnectionNumber.length() != 0) {
            flightTripData.put("FLCONN1", flightConnectionNumber);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added FLCONN1 = '{}' to FLIGHT_TRIP_DATA", flightConnectionNumber);
            }

        }

        // Add Departure Date to request if set
        Date flightConnectionDepartureData = flightConnectionInfo.getDepartureDate();
        if (flightConnectionDepartureData != null) {
            flightTripData.put("FLDATE1", flightConnectionDepartureData);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added FLDATE1 = '{}' to FLIGHT_TRIP_DATA", flightConnectionDepartureData);
            }

        }

        // Add Flight Connection Class to  request.
        // C : Business Class
        // Y : Economy Class
        // F : First Class
        String flightConnectionClass = "Y";
        if (flightConnectionClass != null && flightConnectionClass.length() != 0) {
            flightTripData.put("CLASS", flightConnectionClass);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added CLASS = '{}' to FLIGHT_TRIP_DATA", flightConnectionClass);
            }

        }


        //
        // Add Passenger List Data to request object.
        //
        @SuppressWarnings("unchecked")
        Table<Structure> passengerList = request.get("PASSENGER_LIST", Table.class);
        Structure passengerListEntry = passengerList.add();

        // Add Passenger Form of Address to request if set
        String passengerFormOfAddress = passengerInfo.getFormOfAddress();
        if (passengerFormOfAddress != null && passengerFormOfAddress.length() != 0) {
            passengerListEntry.put("PASSFORM", passengerFormOfAddress);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added PASSFORM = '{}' to PASSENGER_LIST", passengerFormOfAddress);
            }

        }

        // Add Passenger Name to request if set
        String passengerName = passengerInfo.getName();
        if (passengerName != null && passengerName.length() != 0) {
            passengerListEntry.put("PASSNAME", passengerName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added PASSNAME = '{}' to PASSENGER_LIST", passengerName);
            }

        }

        // Add Passenger Data of Birth to request if set
        Date passengerDateOfBirth = passengerInfo.getDateOfBirth();
        if (passengerDateOfBirth != null) {
            passengerListEntry.put("PASSBIRTH", passengerDateOfBirth);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added PASSBIRTH = '{}' to PASSENGER_LIST", passengerDateOfBirth);
            }

        }

        // Put request object into body of exchange message.
        response.getMessage().setBody(request);
        return response;
    }

    @Override
    public Message compose(CamelBindingData source, Exchange exchange) throws Exception {
        Message response = exchange.createMessage();

        // map context properties
        getContextMapper().mapFrom(source, exchange.getContext(response));

        Set<String> attachements = source.getMessage().getAttachmentNames();
        if (!attachements.isEmpty()) {
            for (Entry<String, DataHandler> entry : source.getMessage().getAttachments().entrySet()) {
                response.addAttachment(entry.getKey(), entry.getValue().getDataSource());
            }
        }

        // Retrieve flight connection and passenger info from exchange context property.
        FlightTripRequestInfo flightTripRequestInfo = (FlightTripRequestInfo) exchange.getContext().getProperty(FLIGHT_TRIP_REQUEST_INFO).getValue();
        FlightConnectionInfo flightConnectionInfo = flightTripRequestInfo.getFlightConnectionInfo();
        PassengerInfo passengerInfo = flightTripRequestInfo.getPassengerInfo();

        // Retrieve SAP response object from body of exchange message.
        Structure flightTripCreateResponse = source.getMessage().getBody(Structure.class);

        if (flightTripCreateResponse == null) {
            throw new Exception("No Flight Trip Create Response");
        }

        // Check BAPI return parameter for errors 
        @SuppressWarnings("unchecked")
        Table<Structure> bapiReturn = flightTripCreateResponse.get("RETURN", Table.class);
        Structure bapiReturnEntry = bapiReturn.get(0);
        if (!bapiReturnEntry.get("TYPE", String.class).equals("S")) {
            String message = bapiReturnEntry.get("MESSAGE", String.class);
            throw new Exception("BAPI call failed: " + message);
        }

        // Create bean to hold Flight Booking data.
        BookFlightResponse bookFlightResponse = new BookFlightResponse();

        // Trip Number
        String tripNumber = flightTripCreateResponse.get("TRIPNUMBER", String.class);
        if (tripNumber != null) {
            bookFlightResponse.setTripNumber(tripNumber);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added TRIPNUMBER = '{}' to request", tripNumber);
            }
        } else {
            throw new Exception("No Flight Booking Trip Number");
        }

        // Pricing Info
        Structure ticketPrice = flightTripCreateResponse.get("TICKET_PRICE", Structure.class);
        if (ticketPrice != null) {
            // Ticket Price
            BigDecimal tripPrice = ticketPrice.get("TRIPPRICE", BigDecimal.class);
            bookFlightResponse.setTicketPrice(tripPrice);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added TICKET_PRICE = '{}' to request", tripPrice);
            }
            // Ticket Tax
            BigDecimal tripTax = ticketPrice.get("TRIPTAX", BigDecimal.class);
            bookFlightResponse.setTicketTax(tripTax);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added TICKET_TAX = '{}' to request", tripTax);
            }
            // Currency
            String currency = ticketPrice.get("CURR", String.class); 
            bookFlightResponse.setCurrency(currency);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added CURRENCY = '{}' to request", currency);
            }
        } else {
            throw new Exception("No Flight Booking Ticket Price");
        }

        // Passenger Info
        //  Form
        bookFlightResponse.setPassengerFormOfAddress(passengerInfo.getFormOfAddress());
        //  Name
        bookFlightResponse.setPassengerName(passengerInfo.getName());
        //  DOB
        bookFlightResponse.setPassengerDateOfBirth(passengerInfo.getDateOfBirth());

        // Flight Info
        FlightInfo flightInfo = new FlightInfo();
        //  Flight Time
        flightInfo.setFlightTime(flightConnectionInfo.getFlightTime());
        //  Departure City
        flightInfo.setCityFrom(flightConnectionInfo.getDepartureCity());
        //  Departure Date
        flightInfo.setDepartureDate(flightConnectionInfo.getDepartureDate());
        //  Departure Time
        flightInfo.setDepartureTime(flightConnectionInfo.getDepartureTime());
        //  Arrival City
        flightInfo.setCityTo(flightConnectionInfo.getArrivalCity());
        //  Arrival Date
        flightInfo.setArrivalDate(flightConnectionInfo.getArrivalDate());
        //  Arrival Time
        flightInfo.setArrivalTime(flightConnectionInfo.getArrivalTime());
        bookFlightResponse.setFlightInfo(flightInfo);

        ConnectionInfoTable connectionInfoTable = new ConnectionInfoTable();
        List<ConnectionInfo> rows = new ArrayList<ConnectionInfo>();
        for (FlightHop flightHop: flightConnectionInfo.getFlightHopList()) {
            // Connection Info
            ConnectionInfo connection = new ConnectionInfo();
            //  Connection ID
            connection.setConnectionId(flightHop.getHopNumber());
            //  Airline
            connection.setAirline(flightHop.getAirlineName());
            //  Plane Type
            connection.setPlaneType(flightHop.getAircraftType());
            //  Departure City
            connection.setCityFrom(flightHop.getDepatureCity());
            //  Departure Date
            connection.setDepartureDate(flightHop.getDepatureDate());
            //  Departure Time
            connection.setDepartureTime(flightHop.getDepatureTime());
            //  Arrival City
            connection.setCityTo(flightHop.getArrivalCity());
            //  Arrival Date
            connection.setArrivalDate(flightHop.getArrivalDate());
            //  Arrival Time
            connection.setArrivalTime(flightHop.getArrivalTime());
            rows.add(connection);
        }
        connectionInfoTable.setRows(rows);
        bookFlightResponse.setConnectionInfo(connectionInfoTable);

        response.setContent(bookFlightResponse);
        return response;
    }
}
