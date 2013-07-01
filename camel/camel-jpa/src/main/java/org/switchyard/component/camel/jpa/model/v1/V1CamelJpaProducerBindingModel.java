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
package org.switchyard.component.camel.jpa.model.v1;

import static org.switchyard.component.camel.jpa.model.Constants.JPA_NAMESPACE_V1;

import org.switchyard.component.camel.common.model.v1.V1BaseCamelModel;
import org.switchyard.component.camel.jpa.model.CamelJpaProducerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of jpa producer binding model.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelJpaProducerBindingModel extends V1BaseCamelModel
    implements CamelJpaProducerBindingModel {

    private static final String FLUS_ON_SEND = "flushOnSend";
    private static final String USE_PERSIST = "usePersist";

    /**
     * Create a binding from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelJpaProducerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);

        setModelChildrenOrder(FLUS_ON_SEND, USE_PERSIST);
    }

    /**
     * Creates new binding model.
     */
    public V1CamelJpaProducerBindingModel() {
        super(V1CamelJpaBindingModel.PRODUCE, JPA_NAMESPACE_V1);
    }

    @Override
    public Boolean isFlushOnSend() {
        return getBooleanConfig(FLUS_ON_SEND);
    }

    @Override
    public V1CamelJpaProducerBindingModel setFlushOnSend(Boolean flushOnSend) {
        return setConfig(FLUS_ON_SEND, flushOnSend);
    }

    @Override
    public Boolean isUsePersist() {
        return getBooleanConfig(USE_PERSIST);
    }

    @Override
    public V1CamelJpaProducerBindingModel setUsePersist(Boolean usePersist) {
        return setConfig(USE_PERSIST, usePersist);
    }

}
