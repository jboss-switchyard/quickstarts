/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.camel.sap.model.v2;

import java.net.URI;
import java.util.List;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.sap.model.CamelSapBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
/**
 * A binding for Camel's sap component.
 */
public class V2CamelSapBindingModel extends V1BaseCamelBindingModel
    implements CamelSapBindingModel {

    /**
     * The name of this binding type ("binding.sap").
     */
    public static final String SAP = "sap";

    private static final String SERVER = "server";
    private static final String DESTINATION = "destination";
    private static final String RFC_NAME = "rfcName";
    private static final String TRANSACTED = "transacted";
    /**
     * Create a new V2CamelSapBindingModel.
     * @param namespace namespace
     */
    public V2CamelSapBindingModel(String namespace) {
        super(SAP, namespace);

        setModelChildrenOrder(SERVER, DESTINATION, RFC_NAME, TRANSACTED);
    }

    /**
     * Constructor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V2CamelSapBindingModel(final Configuration config, final Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getServer() {
        return getConfig(SERVER);
    }

    @Override
    public V2CamelSapBindingModel setServer(String server) {
        setConfig(SERVER, server);
        return this;
    }

    @Override
    public String getDestination() {
        return getConfig(DESTINATION);
    }

    @Override
    public V2CamelSapBindingModel setDestination(String destination) {
        setConfig(DESTINATION, destination);
        return this;
    }

    @Override
    public String getRfcName() {
        return getConfig(RFC_NAME);
    }

    @Override
    public V2CamelSapBindingModel setRfcName(String rfcName) {
        setConfig(RFC_NAME, rfcName);
        return this;
    }

    @Override
    public Boolean isTransacted() {
        return getBooleanConfig(TRANSACTED);
    }

    @Override
    public V2CamelSapBindingModel setTransacted(Boolean transacted) {
        setConfig(TRANSACTED, transacted);
        return this;
    }

    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        StringBuilder uriBuf = new StringBuilder(SAP);
        if (isReferenceBinding()) {
            uriBuf.append(":destination:")
                  .append(getDestination());
        } else {
            uriBuf.append(":server:")
                  .append(getServer());
        }
        uriBuf.append(":")
              .append(getRfcName());

        QueryString queryString = new QueryString();
        traverseConfiguration(children, queryString, SERVER, DESTINATION, RFC_NAME);
        uriBuf.append(queryString.toString());

        return URI.create(uriBuf.toString());
    }

}
