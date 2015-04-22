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

import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.fusesource.camel.component.sap.SapConnectionConfiguration;
import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.ServerData;

/**
 * Camel SapConnectionConfiguration producer which setup the required metadata.
 */
public class CamelSAPConnectionConfigurationProducer {

    @Produces
    @Named("sap-configuration")
    public SapConnectionConfiguration create(
            @Named("destinationDataStore") Map<String,DestinationData> destinationDataStore,
            @Named("serverDataStore") Map<String,ServerData> serverDataStore ) {
        SapConnectionConfiguration conf = new SapConnectionConfiguration();
        conf.setDestinationDataStore(destinationDataStore);
        conf.setServerDataStore(serverDataStore);
        return conf;
    }
}
