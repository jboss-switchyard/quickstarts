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

import org.switchyard.component.camel.common.model.file.v1.V1GenericFileConsumerBindingModel;
import org.switchyard.component.camel.common.model.remote.CamelRemoteFileConsumerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Consumer binding model for remote file.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelRemoteFileConsumerBindingModel extends V1GenericFileConsumerBindingModel
    implements CamelRemoteFileConsumerBindingModel {

    /**
     * Creates consumer binding model.
     * 
     * @param config Configuration.
     * @param desc Descriptor.
     */
    public V1CamelRemoteFileConsumerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * Creates model bound to given namespace.
     * 
     * @param namespace Namespace to bound.
     * @param name Name of element.
     */
    public V1CamelRemoteFileConsumerBindingModel(String namespace, String name) {
        super(namespace, name);
    }

}
