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

import org.switchyard.component.camel.netty.model.CamelNettyTcpBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of netty binding.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelNettyTcpBindingModel extends V1CamelNettyBindingModel
    implements CamelNettyTcpBindingModel {

    /**
     * Protocol scheme.
     */
    public static final String TCP = "tcp";

    private static final String TEXTLINE = "textline";
    private static final String TCP_NO_DELAY = "tcpNoDelay";
    private static final String KEEP_ALIVE = "keepAlive";


    /**
     * Create a new CamelNettyTcpBindingModel.
     * @param namespace namespace
     */
    public V1CamelNettyTcpBindingModel(String namespace) {
        super(TCP, namespace);
        setModelChildrenOrder(TEXTLINE, TCP_NO_DELAY, KEEP_ALIVE);
    }

    /**
     * Create a V1CamelNettyBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelNettyTcpBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public Boolean isTextline() {
        return getBooleanConfig(TEXTLINE);
    }

    @Override
    public V1CamelNettyTcpBindingModel setTextline(Boolean textline) {
        return setConfig(TEXTLINE, textline);
    }

    @Override
    public Boolean isTcpNoDelay() {
        return getBooleanConfig(TCP_NO_DELAY);
    }

    @Override
    public V1CamelNettyTcpBindingModel setTcpNoDelay(Boolean tcpNoDelay) {
        return setConfig(TCP_NO_DELAY, tcpNoDelay);
    }

    @Override
    public Boolean isKeepAlive() {
        return getBooleanConfig(KEEP_ALIVE);
    }

    @Override
    public V1CamelNettyTcpBindingModel setKeepAlive(Boolean keepAlive) {
        return setConfig(KEEP_ALIVE, keepAlive);
    }

    @Override
    protected String getProtocol() {
        return TCP;
    }

}
