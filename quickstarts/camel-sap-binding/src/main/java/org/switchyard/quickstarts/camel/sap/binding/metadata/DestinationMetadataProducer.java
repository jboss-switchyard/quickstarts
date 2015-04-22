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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;

/**
 * A destination metadata producer for the camel-sap component.
 */
public class DestinationMetadataProducer {

    @Produces
    @Named("destinationDataStore")
    public Map<String, DestinationData> createDestinationDataStore(
            @Named("nplDestinationData") DestinationData nplDestinationData ) {
        Map<String, DestinationData> store = new HashMap<String, DestinationData>();
        store.put("nplDest", nplDestinationData);
        return store;
    }

    @Produces
    @Named("nplDestinationData")
    public DestinationData createDestinationData() {
        DestinationData data = RfcFactory.eINSTANCE.createDestinationData();
        data.setAshost("nplhost");
        data.setSysnr("42");
        data.setClient("001");
        data.setUser("developer");
        data.setPasswd("ch4ngeme");
        data.setLang("en");
        return data;
    }
}
