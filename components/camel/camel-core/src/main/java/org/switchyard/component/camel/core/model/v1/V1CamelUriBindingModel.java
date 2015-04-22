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
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A generic binding definintion for Apache Camel components.
 * 
 * Example of a binding implemem
 * .tation:
 * <pre>
 * {@code
 *  <sca:service name="SimpleCamelService">
 *     <camel:binding.uri configURI="direct://input">
 *        <switchyard:operationSelector operationName="print"/>
 *     </camel:binding.uri>
 *  </sca:service>
 * }
 * </pre>
 * 
 * <ul>
 * <li> The 'configURI' attribute can be used to specify a Camel component uri.</li>
 * </ul>
 * 
 * @author Daniel Bevenius
 */
public class V1CamelUriBindingModel extends V1BaseCamelBindingModel {

    /**
     * The binding type (binding.camel).
     */
    public static final String URI = "uri";

    /**
     * This is the name of the uri attribute. 
     */
    public static final String CONFIG_URI = "configURI";

    /**
     * This is the name of the transacted attribute. 
     */
    public static final String TRANSACTED = "transacted";

    /**
     * The name of the transacedRef attribute.
     */
    public static final String TRANSACTED_REF = "transactedRef";

    /**
     * Create a new CamelBindingModel.
     * @param namespace namespace
     */
    public V1CamelUriBindingModel(String namespace) {
        super(URI, namespace);
    }

    /**
     * Create a CamelBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelUriBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder();
    }

    /**
     * Gets the binding uri attribute from the underlying model.
     * 
     * @return URI The binding uri attribute.
     */
    public URI getConfigURI() {
        return java.net.URI.create(getModelAttribute(CONFIG_URI));
    }

    /**
     * Sets the "uri" element on the underlying model.
     * 
     * @param uri The Camel Component URI
     * @return {@link org.switchyard.component.camel.common.model.CamelBindingModel CamelBindingModel} to support method chaining.
     */
    public V1CamelUriBindingModel setConfigURI(URI uri) {
        setModelAttribute(CONFIG_URI, uri.toString());
        return this;
    }

    @Override
    public URI getComponentURI() {
        return getConfigURI();
    }

}
