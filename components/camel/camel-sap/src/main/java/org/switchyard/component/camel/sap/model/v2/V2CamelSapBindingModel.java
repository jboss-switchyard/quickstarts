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

import javax.xml.namespace.QName;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.sap.model.CamelSapBindingModel;
import org.switchyard.component.camel.sap.model.CamelSapNamespace;
import org.switchyard.component.camel.sap.model.EndpointModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
/**
 * A binding for Camel's sap component.
 */
public class V2CamelSapBindingModel extends V1BaseCamelBindingModel implements CamelSapBindingModel {

    /**
     * Constructor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V2CamelSapBindingModel(final Configuration config, final Descriptor desc) {
        super(config, desc);
    }

    /**
     * Create a new V2CamelSapBindingModel.
     * @param namespace namespace
     */
    public V2CamelSapBindingModel(String namespace) {
        super(Constants.SAP, namespace);
        setModelChildrenOrder(Constants.IDOCLIST_SERVER, Constants.SRFC_SERVER, Constants.TRFC_SERVER,
                Constants.IDOC_DESTINATION, Constants.IDOCLIST_DESTINATION, Constants.QIDOC_DESTINATION,
                Constants.QIDOCLIST_DESTINATION, Constants.QRFC_DESTINATION, Constants.SRFC_DESTINATION,
                Constants.TRFC_DESTINATION);
    }

    @Override
    public EndpointModel getEndpointModel() {
        for (Model child : getModelChildren()) {
            if (child instanceof EndpointModel) {
                return (EndpointModel) child;
            }
        }
        return null;
    }

    @Override
    public V2CamelSapBindingModel setEndpointModel(EndpointModel endpoint) {
        for (Model child : getModelChildren()) {
            if (child instanceof EndpointModel) {
                EndpointModel endpointChild = EndpointModel.class.cast(child);
                getModelConfiguration().removeChildren(
                        new QName(CamelSapNamespace.V_2_0.uri(), endpointChild.getName()));
                break;
            }
        }
        setChildModel(endpoint);
        return this;
    }

    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        QueryString queryString = new QueryString();
        StringBuilder buf = getEndpointModel().createBaseURIString(queryString);
        traverseConfiguration(children, queryString, getEndpointModel().getName());
        buf.append(queryString.toString());

        return URI.create(buf.toString());
    }

}
