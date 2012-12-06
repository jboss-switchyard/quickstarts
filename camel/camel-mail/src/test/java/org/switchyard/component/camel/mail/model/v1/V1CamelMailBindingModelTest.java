/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.mail.model.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.mail.MailEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;

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
        return new V1CamelMailBindingModel()
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
