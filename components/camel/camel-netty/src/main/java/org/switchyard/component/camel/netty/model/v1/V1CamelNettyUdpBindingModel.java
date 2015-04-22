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

import org.switchyard.component.camel.netty.model.CamelNettyUdpBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of netty udp binding.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelNettyUdpBindingModel extends V1CamelNettyBindingModel
    implements CamelNettyUdpBindingModel {

    /**
     * Protocol scheme.
     */
    public static final String UDP = "udp";

    private static final String BROADCAST = "broadcast";

    /**
     * Create a new CamelNettyUdpBindingModel.
     * @param namespace namespace
     */
    public V1CamelNettyUdpBindingModel(String namespace) {
        super(UDP, namespace);

        setModelChildrenOrder(BROADCAST);
    }

    /**
     * Create a V1CamelNettyBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelNettyUdpBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    protected String getProtocol() {
        return UDP;
    }

    @Override
    public Boolean isBroadcast() {
        return getBooleanConfig(BROADCAST);
    }

    @Override
    public V1CamelNettyUdpBindingModel setBroadcast(Boolean broadcast) {
        return setConfig(BROADCAST, broadcast);
    }

}
