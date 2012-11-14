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
package org.switchyard.component.camel.config.model.mail.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.apache.camel.component.mail.MailEndpoint;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.component.camel.config.model.mail.CamelMailBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelMailBindingModel}.
 */
public class V1CamelMailBindingModelTest extends V1BaseCamelModelTest<V1CamelMailBindingModel> {

    private static final String CAMEL_XML = "switchyard-mail-binding-beans.xml";
    private static final Boolean SECURE = true;

    private static final String HOST = "localhost";
    private static final Integer PORT = 233;
    private static final String USERNAME = "camel";
    private static final String PASSWORD = "rider";
    private static final Integer CONNECTION_TIMEOUT = 300;
    private static final String CAMEL_URI = "imaps://localhost:233?connectionTimeout=300&"
        + "password=rider&unseen=true&username=camel";

    @Test
    public void validateCamelBindingModelWithBeanElement() throws Exception {
        final V1CamelMailBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        validateModel.assertValid();
        assertTrue(validateModel.isValid());
        assertEquals(SECURE, bindingModel.isSecure());
        assertEquals(HOST, bindingModel.getHost());
        assertEquals(PORT, bindingModel.getPort());
        assertEquals(USERNAME, bindingModel.getUsername());
        assertEquals(PASSWORD, bindingModel.getPassword());
        assertEquals(CONNECTION_TIMEOUT, bindingModel.getConnectionTimeout());
    }

    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelBinding(CAMEL_XML).toString();
        String newXml = createModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    @Test
    public void testCamelEndpoint()  throws Exception {
        CamelMailBindingModel model = getFirstCamelBinding(CAMEL_XML);

        MailEndpoint endpoint = getEndpoint(model, MailEndpoint.class);
        assertEquals(HOST, endpoint.getConfiguration().getHost());
        assertEquals(CAMEL_URI, endpoint.getEndpointUri().toString());
    }

    private V1CamelMailBindingModel createModel() {
        return (V1CamelMailBindingModel) new V1CamelMailBindingModel()
            .setSecure(SECURE)
            .setHost(HOST)
            .setPort(PORT)
            .setUsername(USERNAME)
            .setPassword(PASSWORD)
            .setConnectionTimeout(CONNECTION_TIMEOUT)
            .setConsumer(new V1CamelMailConsumerBindingModel().setUnseen(true))
        ;
    }

}
