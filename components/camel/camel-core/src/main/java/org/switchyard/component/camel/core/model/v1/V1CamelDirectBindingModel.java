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
package org.switchyard.component.camel.core.model.v1;

import java.net.URI;

import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.core.model.CamelDirectBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of AtomBindingModel.
 */
public class V1CamelDirectBindingModel extends V1BaseCamelBindingModel
    implements CamelDirectBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String DIRECT = "direct";

    /**
     * Camel endpoint configuration values.
     */
    private static final String NAME = "name";

    /**
     * Create a new CamelDirectBindingModel.
     * @param namespace namespace
     */
    public V1CamelDirectBindingModel(String namespace) {
        super(DIRECT, namespace);

        setModelChildrenOrder(NAME);
    }

    /**
     * Create a CamelDirectBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelDirectBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getEndpointName() {
        return getConfig(NAME);
    }

    @Override
    public V1CamelDirectBindingModel setEndpointName(String name) {
        return setConfig(NAME, name);
    }

    @Override
    public URI getComponentURI() {
        // base URI without params
        String uriStr = DIRECT + "://" + getConfig(NAME);
        return URI.create(uriStr.toString());
    }

}
