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

import org.switchyard.component.camel.common.model.v1.V1BaseCamelMarshaller;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;

/**
 * A Marshaler that is able to read a {@link Configuration} and populate a
 * corresponding Model to the configuration informations.
 * <p>
 * 
 * @author Daniel Bevenius
 */
public class V1CamelCoreModelMarshaller extends V1BaseCamelMarshaller {

    private static final String BINDING_URI = BindingModel.BINDING + '.' + V1CamelUriBindingModel.URI;
    private static final String BINDING_DIRECT = BindingModel.BINDING + '.' + V1CamelDirectBindingModel.DIRECT;
    private static final String BINDING_TIMER = BindingModel.BINDING + '.' + V1CamelTimerBindingModel.TIMER;
    private static final String BINDING_SEDA = BindingModel.BINDING + '.' + V1CamelSedaBindingModel.SEDA;
    private static final String BINDING_MOCK = BindingModel.BINDING + '.' + V1CamelMockBindingModel.MOCK;

    /**
     * Sole constructor.
     * 
     * @param desc The switchyard descriptor.
     */
    public V1CamelCoreModelMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads in the Configuration, looking for various knowledge models.
     * If not found, it falls back to the super class (V1BaseCamelMarshaller).
     *
     * @param config the Configuration
     * @return the Model
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (BINDING_URI.equals(name)) {
            return new V1CamelUriBindingModel(config, desc);
        } else if (BINDING_DIRECT.equals(name)) {
            return new V1CamelDirectBindingModel(config, desc);
        } else if (BINDING_TIMER.equals(name)) {
            return new V1CamelTimerBindingModel(config, desc);
        } else if (BINDING_SEDA.equals(name)) {
            return new V1CamelSedaBindingModel(config, desc);
        } else if (BINDING_MOCK.equals(name)) {
            return new V1CamelMockBindingModel(config, desc);
        }
        return super.read(config);
    }

}
