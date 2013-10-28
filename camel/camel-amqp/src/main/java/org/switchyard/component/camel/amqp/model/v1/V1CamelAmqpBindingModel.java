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
package org.switchyard.component.camel.amqp.model.v1;

import java.net.URI;

import org.switchyard.component.camel.amqp.model.CamelAmqpBindingModel;
import org.switchyard.component.camel.jms.model.v1.V1CamelJmsBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;


/**
 * Implementation of {@link CamelAmqpBindingModel}.
 * 
 * @author: <a href="mailto:eduardo.devera@gmail.com">Eduardo de Vera</a>
 */
public class V1CamelAmqpBindingModel extends V1CamelJmsBindingModel
    implements CamelAmqpBindingModel {

    /**
     * Camel component prefix / binding prefix.
     */
    public static final String AMQP = "amqp";

    /**
     * Default constructor, creates binding using only prefix.
     * @param namespace namespace
     */
    public V1CamelAmqpBindingModel(String namespace) {
        super(AMQP, namespace);
    }

    /**
     * Create a binding from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param descriptor The switchyard descriptor instance.
     */
    public V1CamelAmqpBindingModel(Configuration config, Descriptor descriptor) {
        super(config, descriptor);
    }

    @Override
    public URI getComponentURI() {
        return getComponentURI(AMQP);
    }

}
