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

/**
 * Test for {@link V1CamelNettyBindingModel} checking if it works with sslContextParametersRef.
 *
 * @author Lukasz Dywicki
 */
public class CamelNettyTcpBindingSslContextParameterRefModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelNettyTcpBindingModel, NettyEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-netty-tcp-binding-ssl-config-beans.xml";

    private static final String HOST = "google.com";
    private static final Integer PORT = 10230;
    private static final Boolean SSL = true;
    private static final String SSL_CONTEXT_PARAMETERS_REF = "#sslConfig";

    private static final String CAMEL_URI = "netty:tcp://google.com:10230?" +
        "ssl=true&sslContextParametersRef=#sslConfig";

    public CamelNettyTcpBindingSslContextParameterRefModelTest() {
        super(NettyEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V1CamelNettyTcpBindingModel createTestModel() {
        return (V1CamelNettyTcpBindingModel) new V1CamelNettyTcpBindingModel()
            .setHost(HOST)
            .setPort(PORT)
            .setSsl(SSL)
            .setSslContextParametersRef(SSL_CONTEXT_PARAMETERS_REF);
    }

    @Override
    protected void createModelAssertions(V1CamelNettyTcpBindingModel model) {
        assertEquals(HOST, model.getHost());
        assertEquals(PORT, model.getPort());
        assertEquals(SSL, model.isSsl());
        assertEquals(SSL_CONTEXT_PARAMETERS_REF, model.getSslContextParametersRef());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }
}