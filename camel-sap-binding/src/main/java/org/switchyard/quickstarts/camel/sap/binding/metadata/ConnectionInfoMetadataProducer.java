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
 * A connection info metadata producer.
 */
public class ConnectionInfoMetadataProducer {
    @Produces
    @Named("connectionInfo")
    public RecordMetaData createConnectionInfo(
            @Named("connectionInfoFieldMetaData") List<FieldMetaData> connectionInfoFieldMetaData ) {
        RecordMetaData conninfo = RfcFactory.eINSTANCE.createRecordMetaData();
        conninfo.setName("CONNECTION_INFO");
        conninfo.setRecordFieldMetaData(connectionInfoFieldMetaData);
        return conninfo;
    }

    @Produces
    @Named("connectionInfoFieldMetaData")
    public List<FieldMetaData> createConnectionInfoFieldList() {
        List<FieldMetaData> connFields = new ArrayList<FieldMetaData>();
        FieldMetaData connid = RfcFactory.eINSTANCE.createFieldMetaData();
        connid.setName("CONNID");
        connid.setType(DataType.NUM);
        connid.setByteLength(1);
        connid.setByteOffset(0);
        connid.setUnicodeByteLength(2);
        connid.setUnicodeByteOffset(0);
        connFields.add(connid);
        FieldMetaData airline = RfcFactory.eINSTANCE.createFieldMetaData();
        airline.setName("AIRLINE");
        airline.setType(DataType.CHAR);
        airline.setByteLength(20);
        airline.setByteOffset(1);
        airline.setUnicodeByteLength(40);
        airline.setUnicodeByteOffset(2);
        connFields.add(airline);
        FieldMetaData planetype = RfcFactory.eINSTANCE.createFieldMetaData();
        planetype.setName("PLANETYPE");
        planetype.setType(DataType.CHAR);
        planetype.setByteLength(10);
        planetype.setByteOffset(21);
        planetype.setUnicodeByteLength(20);
        planetype.setUnicodeByteOffset(42);
        connFields.add(planetype);
        FieldMetaData cityfrom = RfcFactory.eINSTANCE.createFieldMetaData();
        cityfrom.setName("CITYFROM");
        cityfrom.setType(DataType.CHAR);
        cityfrom.setByteLength(20);
        cityfrom.setByteOffset(31);
        cityfrom.setUnicodeByteLength(40);
        cityfrom.setUnicodeByteOffset(62);
        connFields.add(cityfrom);
        FieldMetaData depdate = RfcFactory.eINSTANCE.createFieldMetaData();
        depdate.setName("DEPDATE");
        depdate.setType(DataType.DATE);
        depdate.setByteLength(8);
        depdate.setByteOffset(51);
        depdate.setUnicodeByteLength(16);
        depdate.setUnicodeByteOffset(102);
        connFields.add(depdate);
        FieldMetaData deptime = RfcFactory.eINSTANCE.createFieldMetaData();
        deptime.setName("DEPTIME");
        deptime.setType(DataType.TIME);
        deptime.setByteLength(6);
        deptime.setByteOffset(59);
        deptime.setUnicodeByteLength(12);
        deptime.setUnicodeByteOffset(118);
        connFields.add(deptime);
        FieldMetaData cityto = RfcFactory.eINSTANCE.createFieldMetaData();
        cityto.setName("CITYTO");
        cityto.setType(DataType.CHAR);
        cityto.setByteLength(20);
        cityto.setByteOffset(65);
        cityto.setUnicodeByteLength(40);
        cityto.setUnicodeByteOffset(130);
        connFields.add(cityto);
        FieldMetaData arrdate = RfcFactory.eINSTANCE.createFieldMetaData();
        arrdate.setName("ARRDATE");
        arrdate.setType(DataType.DATE);
        arrdate.setByteLength(8);
        arrdate.setByteOffset(85);
        arrdate.setUnicodeByteLength(16);
        arrdate.setUnicodeByteOffset(170);
        connFields.add(arrdate);
        FieldMetaData arrtime = RfcFactory.eINSTANCE.createFieldMetaData();
        arrtime.setName("ARRTIME");
        arrtime.setType(DataType.TIME);
        arrtime.setByteLength(6);
        arrtime.setByteOffset(93);
        arrtime.setUnicodeByteLength(12);
        arrtime.setUnicodeByteOffset(186);
        connFields.add(arrtime);
        return connFields;
    }
}
