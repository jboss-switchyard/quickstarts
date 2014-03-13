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
import org.fusesource.camel.component.sap.model.rfc.FieldMetaData;
import org.fusesource.camel.component.sap.model.rfc.RecordMetaData;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;

/**
 * A flight info metadata producer.
 */
public class FlightInfoMetadataProducer {

    @Produces
    @Named("flightInfo")
    public RecordMetaData createFlightInfo(
            @Named("flightInfoFieldMetaData") List<FieldMetaData> flightInfoFieldMetaData ) {
        RecordMetaData flightinfo = RfcFactory.eINSTANCE.createRecordMetaData();
        flightinfo.setName("FLTINFO_STRUCTURE");
        flightinfo.setRecordFieldMetaData(flightInfoFieldMetaData);
        return flightinfo;
    }

    @Produces
    @Named("flightInfoFieldMetaData")
    public List<FieldMetaData> createFlightInfoFieldList() {
        List<FieldMetaData> flightFields = new ArrayList<FieldMetaData>();
        FieldMetaData flighttime = RfcFactory.eINSTANCE.createFieldMetaData();
        flighttime.setName("FLIGHTTIME");
        flighttime.setType(DataType.NUM);
        flighttime.setByteLength(10);
        flighttime.setByteOffset(0);
        flighttime.setUnicodeByteLength(20);
        flighttime.setUnicodeByteOffset(0);
        flightFields.add(flighttime);
        FieldMetaData cityfrom = RfcFactory.eINSTANCE.createFieldMetaData();
        cityfrom.setName("CITYFROM");
        cityfrom.setType(DataType.CHAR);
        cityfrom.setByteLength(20);
        cityfrom.setByteOffset(10);
        cityfrom.setUnicodeByteLength(40);
        cityfrom.setUnicodeByteOffset(20);
        flightFields.add(cityfrom);
        FieldMetaData depdate = RfcFactory.eINSTANCE.createFieldMetaData();
        depdate.setName("DEPDATE");
        depdate.setType(DataType.DATE);
        depdate.setByteLength(8);
        depdate.setByteOffset(30);
        depdate.setUnicodeByteLength(16);
        depdate.setUnicodeByteOffset(60);
        flightFields.add(depdate);
        FieldMetaData deptime = RfcFactory.eINSTANCE.createFieldMetaData();
        deptime.setName("DEPTIME");
        deptime.setType(DataType.TIME);
        deptime.setByteLength(6);
        deptime.setByteOffset(38);
        deptime.setUnicodeByteLength(12);
        deptime.setUnicodeByteOffset(76);
        flightFields.add(deptime);
        FieldMetaData cityto = RfcFactory.eINSTANCE.createFieldMetaData();
        cityto.setName("CITYTO");
        cityto.setType(DataType.CHAR);
        cityto.setByteLength(20);
        cityto.setByteOffset(44);
        cityto.setUnicodeByteLength(40);
        cityto.setUnicodeByteOffset(88);
        flightFields.add(cityto);
        FieldMetaData arrdate = RfcFactory.eINSTANCE.createFieldMetaData();
        arrdate.setName("ARRDATE");
        arrdate.setType(DataType.DATE);
        arrdate.setByteLength(8);
        arrdate.setByteOffset(64);
        arrdate.setUnicodeByteLength(16);
        arrdate.setUnicodeByteOffset(128);
        flightFields.add(arrdate);
        FieldMetaData arrtime = RfcFactory.eINSTANCE.createFieldMetaData();
        arrtime.setName("ARRTIME");
        arrtime.setType(DataType.TIME);
        arrtime.setByteLength(6);
        arrtime.setByteOffset(72);
        arrtime.setUnicodeByteLength(12);
        arrtime.setUnicodeByteOffset(144);
        flightFields.add(arrtime);
        return flightFields;
    }
}
