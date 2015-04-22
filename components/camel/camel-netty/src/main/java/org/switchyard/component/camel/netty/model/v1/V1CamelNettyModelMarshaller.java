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
package org.switchyard.component.camel.netty.model.v1;

import org.switchyard.component.camel.common.model.v1.V1BaseCamelMarshaller;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;

/**
 * Netty tcp & udp model marshaller.
 */
public class V1CamelNettyModelMarshaller extends V1BaseCamelMarshaller {

    private static final String BINDING_TCP = BindingModel.BINDING + '.' + V1CamelNettyTcpBindingModel.TCP;
    private static final String BINDING_UDP = BindingModel.BINDING + '.' + V1CamelNettyUdpBindingModel.UDP;

    /**
     * Creates new marshaller.
     * 
     * @param desc Descriptor
     */
    public V1CamelNettyModelMarshaller(Descriptor desc) {
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
        if (BINDING_TCP.equals(name)) {
            return new V1CamelNettyTcpBindingModel(config, desc);
        } else if (BINDING_UDP.equals(name)) {
            return new V1CamelNettyUdpBindingModel(config, desc);
        }
        return super.read(config);
    }

}
