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
package org.switchyard.component.camel.file.model.v1;

import static org.switchyard.component.camel.file.model.Constants.FILE_NAMESPACE_V1;

import org.switchyard.component.camel.common.model.file.v1.V1GenericFileConsumerBindingModel;
import org.switchyard.component.camel.file.model.CamelFileConsumerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A binding for Camel's file component, for consumer configs.
 * 
 * @author Mario Antollini
 */
public class V1CamelFileConsumerBindingModel extends V1GenericFileConsumerBindingModel
    implements CamelFileConsumerBindingModel {

    /**
     * Create a new V1CamelFileConsumerBindingModel.
     */
    public V1CamelFileConsumerBindingModel() {
        super(V1CamelFileBindingModel.CONSUME, FILE_NAMESPACE_V1);
    }

    /**
     * Create a V1CamelFileConsumerBindingModel from the specified configuration
     * and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelFileConsumerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

}
