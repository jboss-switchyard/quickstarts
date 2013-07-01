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
package org.switchyard.component.camel.quartz.model.v1;

import static org.switchyard.component.camel.quartz.model.CamelQuartzBindingModel.QUARTZ;
import static org.switchyard.component.camel.quartz.Constants.QUARTZ_NAMESPACE_V1;

import org.switchyard.component.camel.common.marshaller.BaseModelMarshaller;
import org.switchyard.component.camel.common.marshaller.ModelCreator;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Camel quartz v1 model marshaler.
 */
public class V1CamelQuartzModelMarshaller extends BaseModelMarshaller {

    /**
     * Creates new marshaller.
     * 
     * @param desc Descriptor
     */
    public V1CamelQuartzModelMarshaller(Descriptor desc) {
        super(desc, QUARTZ_NAMESPACE_V1);

        registerBinding(QUARTZ, new ModelCreator<V1CamelQuartzBindingModel>() {
            @Override
            public V1CamelQuartzBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelQuartzBindingModel(config, descriptor);
            }
        });
    }

}
