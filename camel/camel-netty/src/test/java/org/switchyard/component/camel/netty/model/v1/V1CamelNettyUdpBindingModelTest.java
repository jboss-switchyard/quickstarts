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

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.netty.NettyEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.netty.model.CamelNettyNamespace;

/**
 * Test for {@link V1CamelNettyBindingModel}.
 *
 * @author Lukasz Dywicki
 */
public class V1CamelNettyUdpBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelNettyUdpBindingModel, NettyEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-netty-udp-binding-beans.xml";

    private static final String HOST = "google.com";
    private static final Integer PORT = 10231;
    private static final Boolean BROADCAST = true;

    private static final String CAMEL_URI = "netty:udp://google.com:10231?broadcast=true";

    public V1CamelNettyUdpBindingModelTest() {
        super(NettyEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V1CamelNettyUdpBindingModel createTestModel() {
        return ((V1CamelNettyUdpBindingModel) new V1CamelNettyUdpBindingModel(CamelNettyNamespace.V_1_0.uri())
            .setHost(HOST)
            .setPort(PORT))
            .setBroadcast(BROADCAST);
    }

    @Override
    protected void createModelAssertions(V1CamelNettyUdpBindingModel model) {
        assertEquals(HOST, model.getHost());
        assertEquals(PORT, model.getPort());
        assertEquals(BROADCAST, model.isBroadcast());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }
}