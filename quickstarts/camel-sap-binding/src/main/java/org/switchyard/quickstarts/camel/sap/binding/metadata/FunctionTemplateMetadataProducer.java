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
package org.switchyard.quickstarts.camel.sap.binding.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.fusesource.camel.component.sap.model.rfc.DataType;
import org.fusesource.camel.component.sap.model.rfc.FunctionTemplate;
import org.fusesource.camel.component.sap.model.rfc.ListFieldMetaData;
import org.fusesource.camel.component.sap.model.rfc.RecordMetaData;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;

/**
 * A FunctionTemplate metadata producer for the camel-sap component.
 */
public class FunctionTemplateMetadataProducer {

    @Produces
    @Named("bookFlightFunctionTemplate")
    public FunctionTemplate createBookFlightFunctionTemplate(
            @Named("importParameterList") List<ListFieldMetaData> importParameterList,
            @Named("exportParameterList") List<ListFieldMetaData> exportParameterList ) {
        FunctionTemplate template = RfcFactory.eINSTANCE.createFunctionTemplate();
        template.setImportParameterList(importParameterList);
        template.setExportParameterList(exportParameterList);
        return template;
    }

    @Produces
    @Named("importParameterList")
    private List<ListFieldMetaData> createImportParameterList() {
        List<ListFieldMetaData> imports = new ArrayList<ListFieldMetaData>();
        ListFieldMetaData custname = RfcFactory.eINSTANCE.createListFieldMetaData();
        custname.setName("CUSTNAME");
        custname.setType(DataType.CHAR);
        custname.setByteLength(25);
        custname.setUnicodeByteLength(50);
        imports.add(custname);
        ListFieldMetaData passform = RfcFactory.eINSTANCE.createListFieldMetaData();
        passform.setName("PASSFORM");
        passform.setType(DataType.CHAR);
        passform.setByteLength(15);
        passform.setUnicodeByteLength(30);
        imports.add(passform);
        ListFieldMetaData passname = RfcFactory.eINSTANCE.createListFieldMetaData();
        passname.setName("PASSNAME");
        passname.setType(DataType.CHAR);
        passname.setByteLength(25);
        passname.setUnicodeByteLength(50);
        imports.add(passname);
        ListFieldMetaData passbirth = RfcFactory.eINSTANCE.createListFieldMetaData();
        passbirth.setName("PASSBIRTH");
        passbirth.setType(DataType.DATE);
        passbirth.setByteLength(8);
        passbirth.setUnicodeByteLength(16);
        imports.add(passbirth);
        ListFieldMetaData flightdate = RfcFactory.eINSTANCE.createListFieldMetaData();
        flightdate.setName("FLIGHTDATE");
        flightdate.setType(DataType.DATE);
        flightdate.setByteLength(8);
        flightdate.setUnicodeByteLength(16);
        imports.add(flightdate);
        ListFieldMetaData travelagencynumber = RfcFactory.eINSTANCE.createListFieldMetaData();
        travelagencynumber.setName("TRAVELAGENCYNUMBER");
        travelagencynumber.setType(DataType.NUM);
        travelagencynumber.setByteLength(8);
        travelagencynumber.setUnicodeByteLength(8);
        imports.add(travelagencynumber);
        ListFieldMetaData destinationFrom = RfcFactory.eINSTANCE.createListFieldMetaData();
        destinationFrom.setName("DESTINATION_FROM");
        destinationFrom.setType(DataType.CHAR);
        destinationFrom.setByteLength(3);
        destinationFrom.setUnicodeByteLength(6);
        imports.add(destinationFrom);
        ListFieldMetaData destinationTo = RfcFactory.eINSTANCE.createListFieldMetaData();
        destinationTo.setName("DESTINATION_TO");
        destinationTo.setType(DataType.CHAR);
        destinationTo.setByteLength(3);
        destinationTo.setUnicodeByteLength(6);
        imports.add(destinationTo);
        return imports;
    }

    @Produces
    @Named("exportParameterList")
    public List<ListFieldMetaData> createExportParameterList(
            @Named("flightInfo") RecordMetaData flightInfo,
            @Named("connectionInfo") RecordMetaData connectionInfo ) {
        List<ListFieldMetaData> exports = new ArrayList<ListFieldMetaData>();
        ListFieldMetaData tripnumber = RfcFactory.eINSTANCE.createListFieldMetaData();
        tripnumber.setName("TRIPNUMBER");
        tripnumber.setType(DataType.NUM);
        tripnumber.setByteLength(8);
        tripnumber.setUnicodeByteLength(16);
        exports.add(tripnumber);
        ListFieldMetaData ticketPrice = RfcFactory.eINSTANCE.createListFieldMetaData();
        ticketPrice.setName("TICKET_PRICE");
        ticketPrice.setType(DataType.BCD);
        ticketPrice.setByteLength(12);
        ticketPrice.setUnicodeByteLength(12);
        ticketPrice.setDecimals(4);
        exports.add(ticketPrice);
        ListFieldMetaData ticketTax = RfcFactory.eINSTANCE.createListFieldMetaData();
        ticketTax.setName("TICKET_TAX");
        ticketTax.setType(DataType.BCD);
        ticketTax.setByteLength(12);
        ticketTax.setUnicodeByteLength(12);
        ticketTax.setDecimals(4);
        exports.add(ticketTax);
        ListFieldMetaData currency = RfcFactory.eINSTANCE.createListFieldMetaData();
        currency.setName("CURRENCY");
        currency.setType(DataType.CHAR);
        currency.setByteLength(5);
        currency.setUnicodeByteLength(10);
        exports.add(currency);
        ListFieldMetaData passform = RfcFactory.eINSTANCE.createListFieldMetaData();
        passform.setName("PASSFORM");
        passform.setType(DataType.CHAR);
        passform.setByteLength(15);
        passform.setUnicodeByteLength(30);
        exports.add(passform);
        ListFieldMetaData passname = RfcFactory.eINSTANCE.createListFieldMetaData();
        passname.setName("PASSNAME");
        passname.setType(DataType.CHAR);
        passname.setByteLength(25);
        passname.setUnicodeByteLength(50);
        exports.add(passname);
        ListFieldMetaData passbirth = RfcFactory.eINSTANCE.createListFieldMetaData();
        passbirth.setName("PASSBIRTH");
        passbirth.setType(DataType.DATE);
        passbirth.setByteLength(8);
        passbirth.setUnicodeByteLength(16);
        exports.add(passbirth);
        ListFieldMetaData fltinfo = RfcFactory.eINSTANCE.createListFieldMetaData();
        fltinfo.setName("FLTINFO");
        fltinfo.setType(DataType.STRUCTURE);
        fltinfo.setRecordMetaData(flightInfo);
        exports.add(fltinfo);
        ListFieldMetaData conninfo = RfcFactory.eINSTANCE.createListFieldMetaData();
        conninfo.setName("CONNINFO");
        conninfo.setType(DataType.TABLE);
        conninfo.setRecordMetaData(connectionInfo);
        exports.add(conninfo);
        return exports;
    }
}
