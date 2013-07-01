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

import static org.switchyard.component.camel.core.model.Constants.CORE_NAMESPACE_V1;

import org.switchyard.component.camel.common.marshaller.BaseModelMarshaller;
import org.switchyard.component.camel.common.marshaller.ModelCreator;
import org.switchyard.component.camel.core.model.direct.v1.V1CamelDirectBindingModel;
import org.switchyard.component.camel.core.model.mock.v1.V1CamelMockBindingModel;
import org.switchyard.component.camel.core.model.seda.v1.V1CamelSedaBindingModel;
import org.switchyard.component.camel.core.model.timer.v1.V1CamelTimerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A Marshaler that is able to read a {@link Configuration} and populate a
 * corresponding Model to the configuration informations.
 * <p>
 * 
 * @author Daniel Bevenius
 */
public class V1CamelCoreModelMarshaller extends BaseModelMarshaller {

    /**
     * Sole constructor.
     * 
     * @param desc The switchyard descriptor.
     */
    public V1CamelCoreModelMarshaller(Descriptor desc) {
        super(desc, CORE_NAMESPACE_V1);

        registerBinding(V1CamelBindingModel.URI, new ModelCreator<V1CamelBindingModel>() {
            @Override
            public V1CamelBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelBindingModel(config, descriptor);
            }
        });
        registerBinding(V1CamelDirectBindingModel.DIRECT, new ModelCreator<V1CamelDirectBindingModel>() {
            @Override
            public V1CamelDirectBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelDirectBindingModel(config, descriptor);
            }
        });
        registerBinding(V1CamelTimerBindingModel.TIMER, new ModelCreator<V1CamelTimerBindingModel>() {
            @Override
            public V1CamelTimerBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelTimerBindingModel(config, descriptor);
            }
        });
        registerBinding(V1CamelSedaBindingModel.SEDA, new ModelCreator<V1CamelSedaBindingModel>() {
            @Override
            public V1CamelSedaBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelSedaBindingModel(config, descriptor);
            }
        });
        registerBinding(V1CamelMockBindingModel.MOCK, new ModelCreator<V1CamelMockBindingModel>() {
            @Override
            public V1CamelMockBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelMockBindingModel(config, descriptor);
            }
        });
    }

}
