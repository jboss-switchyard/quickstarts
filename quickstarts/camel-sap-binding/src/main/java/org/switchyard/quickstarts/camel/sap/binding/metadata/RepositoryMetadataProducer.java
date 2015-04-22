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

import org.fusesource.camel.component.sap.model.rfc.FunctionTemplate;
import org.fusesource.camel.component.sap.model.rfc.RepositoryData;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;

/**
 * A repository metadata producer for the camel-sap component.
 */
public class RepositoryMetadataProducer {

    @Produces
    @Named("repositoryDataStore")
    public Map<String, RepositoryData> createRepositoryDataStore(
            @Named("nplRepositoryData") RepositoryData nplRepositoryData ) {
        Map<String, RepositoryData> store = new HashMap<String, RepositoryData>();
        store.put("nplServer", nplRepositoryData);
        return store;
    }

    @Produces
    @Named("nplRepositoryData")
    public RepositoryData createRepositoryData(
            @Named("bookFlightFunctionTemplate") FunctionTemplate bookFlightFunctionTemplate ) {
        RepositoryData data = RfcFactory.eINSTANCE.createRepositoryData();
        Map<String, FunctionTemplate> templates = new HashMap<String, FunctionTemplate>();
        templates.put("BOOK_FLIGHT", bookFlightFunctionTemplate);
        data.setFunctionTemplates(templates);
        return data;
    }
}
