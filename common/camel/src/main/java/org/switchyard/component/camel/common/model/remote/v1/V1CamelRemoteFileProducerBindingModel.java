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
package org.switchyard.component.camel.common.model.remote.v1;

import org.switchyard.component.camel.common.model.file.v1.V1GenericFileProducerBindingModel;
import org.switchyard.component.camel.common.model.remote.CamelRemoteFileProducerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Binding model for remote producers.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelRemoteFileProducerBindingModel extends V1GenericFileProducerBindingModel
    implements CamelRemoteFileProducerBindingModel {

    /**
     * Creates model bound to given namespace.
     * 
     * @param name Name of element.
     * @param namespace Namespace to bound.
     */
    public V1CamelRemoteFileProducerBindingModel(String name, String namespace) {
        super(name, namespace);
    }

    /**
     * Create a binding model from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelRemoteFileProducerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

}
