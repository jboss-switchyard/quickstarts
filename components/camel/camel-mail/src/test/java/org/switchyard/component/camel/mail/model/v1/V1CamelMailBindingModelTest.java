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
package org.switchyard.component.camel.mail.model.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.mail.MailEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.mail.model.CamelMailNamespace;

/**
 * Test for {@link V1CamelMailBindingModel}.
 */
public class V1CamelMailBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelMailBindingModel, MailEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-mail-binding-beans.xml";
    private static final Boolean SECURE = true;

    private static final String HOST = "localhost";
    private static final Integer PORT = 233;
    private static final String USERNAME = "camel";
    private static final String PASSWORD = "rider";
    private static final Integer CONNECTION_TIMEOUT = 300;
    private static final String CAMEL_URI = "imaps://localhost:233?connectionTimeout=300&"
        + "password=rider&username=camel";

    public V1CamelMailBindingModelTest() {
        super(MailEndpoint.class, CAMEL_XML);
    }

    @Override
    protected void createModelAssertions(V1CamelMailBindingModel model) {
        assertEquals(SECURE, model.isSecure());
        assertEquals(HOST, model.getHost());
        assertEquals(PORT, model.getPort());
        assertEquals(USERNAME, model.getUsername());
        assertEquals(PASSWORD, model.getPassword());
        assertEquals(CONNECTION_TIMEOUT, model.getConnectionTimeout());
    }

    @Override
    protected V1CamelMailBindingModel createTestModel() {
        return new V1CamelMailBindingModel(CamelMailNamespace.V_1_0.uri())
            .setSecure(SECURE)
            .setHost(HOST)
            .setPort(PORT)
            .setUsername(USERNAME)
            .setPassword(PASSWORD)
            .setConnectionTimeout(CONNECTION_TIMEOUT)
        ;
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
