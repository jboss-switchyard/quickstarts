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
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelMessageComposer;
import org.switchyard.quickstarts.camel.sap.binding.bean.FlightCustomerInfo;
import org.switchyard.quickstarts.camel.sap.binding.jaxb.BookFlightRequest;

/**
 * A MessageComposer for GetFlightCustomerListService.
 */
public class FlightCustomerInfoMessageComposer extends CamelMessageComposer {
    private static final Logger LOG = LoggerFactory.getLogger(FlightCustomerInfoMessageComposer.class);

    @Override
    public CamelBindingData decompose(Exchange exchange, CamelBindingData target) throws Exception {
        CamelBindingData response = super.decompose(exchange, target);

        // Get BOOK_FLIGHT Request JAXB Bean object.
        BookFlightRequest bookFlightRequest = exchange.getMessage().getContent(BookFlightRequest.class);

        // Create SAP Request object from target endpoint.
        SapSynchronousRfcDestinationEndpoint endpoint = target.getMessage().getExchange().getContext().getEndpoint("sap-srfc-destination:nplDest:BAPI_FLCUST_GETLIST", SapSynchronousRfcDestinationEndpoint.class);
        Structure request = endpoint.createRequest();

        // Add Customer Name to request if set
        if (bookFlightRequest.getCustomerName() != null && bookFlightRequest.getCustomerName().length() > 0) {
            request.put("CUSTOMER_NAME", bookFlightRequest.getCustomerName());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added CUSTOMER_NAME = '{}' to request", bookFlightRequest.getCustomerName());
            }
        } else {
            throw new Exception("No Customer Name");
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

        // Retrieve SAP response object from body of exchange message.
        Structure flightCustomerGetListResponse = source.getMessage().getBody(Structure.class);

        if (flightCustomerGetListResponse == null) {
            throw new Exception("No Flight Customer Get List Response");
        }

        // Check BAPI return parameter for errors 
        @SuppressWarnings("unchecked")
        Table<Structure> bapiReturn = flightCustomerGetListResponse.get("RETURN", Table.class);
        Structure bapiReturnEntry = bapiReturn.get(0);
        if (!bapiReturnEntry.get("TYPE", String.class).equals("S")) {
            String message = bapiReturnEntry.get("MESSAGE", String.class);
            throw new Exception("BAPI call failed: " + message);
        }

        // Get customer list table from response object.
        @SuppressWarnings("unchecked")
        Table<? extends Structure> customerList = flightCustomerGetListResponse.get("CUSTOMER_LIST", Table.class);

        if (customerList == null || customerList.size() == 0) {
            throw new Exception("No Customer Info.");
        }

        // Get Flight Customer data from first row of table.
        Structure customer = customerList.get(0);

        // Create bean to hold Flight Customer data.
        FlightCustomerInfo flightCustomerInfo = new FlightCustomerInfo();

        // Get customer id from Flight Customer data and add to bean.
        String customerId = customer.get("CUSTOMERID", String.class);
        if (customerId != null) {
            flightCustomerInfo.setCustomerNumber(customerId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set customer number = '{}' in flight customer info", customerId);
            }
        }

        // Get customer name from Flight Customer data and add to bean.
        String customerName = customer.get("CUSTNAME", String.class);
        if (customerName != null) {
            flightCustomerInfo.setName(customerName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set customer name = '{}' in flight customer info", customerName);
            }
        }

        // Get customer form of address from Flight Customer data and add to bean.
        String formOfAddress = customer.get("FORM", String.class);
        if (formOfAddress != null) {
            flightCustomerInfo.setFormOfAddress(formOfAddress);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set form of address = '{}' in flight customer info", formOfAddress);
            }
        }

        // Get customer street name from Flight Customer data and add to bean.
        String street = customer.get("STREET", String.class);
        if (street != null) {
            flightCustomerInfo.setStreet(street);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set street = '{}' in flight customer info", street);
            }
        }

        // Get customer PO box from Flight Customer data and add to bean.
        String poBox = customer.get("POBOX", String.class);
        if (poBox != null) {
            flightCustomerInfo.setPoBox(poBox);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set PO box = '{}' in flight customer info", poBox);
            }
        }

        // Get customer postal code from Flight Customer data and add to bean.
        String postalCode = customer.get("POSTCODE", String.class);
        if (postalCode != null) {
            flightCustomerInfo.setPostalCode(postalCode);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set postal code = '{}' in flight customer info", postalCode);
            }
        }

        // Get customer city name from Flight Customer data and add to bean.
        String city = customer.get("CITY", String.class);
        if (city != null) {
            flightCustomerInfo.setCity(city);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set city = '{}' in flight customer info", city);
            }
        }

        // Get customer country name from Flight Customer data and add to bean.
        String country = customer.get("COUNTR", String.class);
        if (country != null) {
            flightCustomerInfo.setCountry(country);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set country = '{}' in flight customer info", country);
            }
        }

        // Get customer country ISO code from Flight Customer data and add to bean.
        String countryIso = customer.get("COUNTR_ISO", String.class);
        if (countryIso != null) {
            flightCustomerInfo.setCountryIso(countryIso);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set iso country code = '{}' in flight customer info", countryIso);
            }
        }

        // Get customer region name from Flight Customer data and add to bean.
        String region = customer.get("REGION", String.class);
        if (region != null) {
            flightCustomerInfo.setRegion(region);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set region = '{}' in flight customer info", region);
            }
        }

        // Get customer phone number from Flight Customer data and add to bean.
        String phone = customer.get("PHONE", String.class);
        if (phone != null) {
            flightCustomerInfo.setPhone(phone);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set phone = '{}' in flight customer info", phone);
            }
        }

        // Get customer email from Flight Customer data and add to bean.
        String email = customer.get("EMAIL", String.class);
        if (email != null) {
            flightCustomerInfo.setEmail(email);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set email = '{}' in flight customer info", email);
            }
        }

        // Put flight customer info bean
        response.setContent(flightCustomerInfo);
        return response;
    }

}
