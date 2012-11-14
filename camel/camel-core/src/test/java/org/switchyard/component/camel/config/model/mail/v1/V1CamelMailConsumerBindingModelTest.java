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
import org.switchyard.component.camel.config.model.mail.CamelMailConsumerBindingModel;
import org.switchyard.component.camel.config.model.mail.v1.V1CamelMailConsumerBindingModel.AccountType;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelMailBindingModel} with {@link V1CamelMailConsumerBindingModel} set.
 */
public class V1CamelMailConsumerBindingModelTest extends V1BaseCamelModelTest<V1CamelMailBindingModel> {

    private static final String CAMEL_XML = "switchyard-mail-binding-consumer-beans.xml";

    private static final String HOST = "localhost";
    private static final Boolean SECURE = true;

    private static final String FOLDER_NAME = "Mail/Inbox";
    private static final Integer FETCH_SIZE = 10;
    private static final Boolean UNSEEN = false;
    private static final Boolean DELETE = true;
    private static final String COPY_TO = "SEEN";
    private static final Boolean DISCONNECT = true;
    private static final String ACCOUNT_TYPE = AccountType.pop3.name();

    private static final String CAMEL_URI = "pop3s://localhost?folderName=Mail/Inbox&fetchSize=10&unseen=false&delete=true&copyTo=SEEN&disconnect=true";

    @Test
    public void testConfigOverride() {
        // set a value on an existing config element
        V1CamelMailBindingModel bindingModel = createModel();

        bindingModel.getConsumer().setFetchSize(2500);
        assertEquals(new Integer(2500), bindingModel.getConsumer().getFetchSize());
    }

    @Test
    public void testReadConfig() throws Exception {
        final V1CamelMailBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        assertTrue(validateModel.isValid());
        //Camel File
        assertEquals(HOST, bindingModel.getHost());
        assertEquals(SECURE, bindingModel.isSecure());
        //Camel File Consumer
        assertConsumerModel(bindingModel.getConsumer());
        assertEquals(CAMEL_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void testWriteConfig() throws Exception {
        V1CamelMailBindingModel bindingModel = createModel();
        assertEquals(HOST, bindingModel.getHost());
        assertEquals(SECURE, bindingModel.isSecure());
        //Camel File Consumer
        assertConsumerModel(bindingModel.getConsumer());
        assertEquals(CAMEL_URI, bindingModel.getComponentURI().toString());
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
    public void testComponentURI() {
        V1CamelMailBindingModel bindingModel = createModel();
        assertEquals(CAMEL_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void testCamelEndpoint() {
        V1CamelMailBindingModel model = createModel();
        MailEndpoint endpoint = getEndpoint(model, MailEndpoint.class);

        assertEquals(HOST, endpoint.getConfiguration().getHost());
        assertEquals(FOLDER_NAME, endpoint.getConfiguration().getFolderName());
        assertEquals(FETCH_SIZE.intValue(), endpoint.getConfiguration().getFetchSize());
        assertEquals(UNSEEN.booleanValue(), endpoint.getConfiguration().isUnseen());
        assertEquals(DELETE.booleanValue(), endpoint.getConfiguration().isDelete());
        assertEquals(COPY_TO, endpoint.getConfiguration().getCopyTo());
        assertEquals(DISCONNECT.booleanValue(), endpoint.getConfiguration().isDisconnect());
    }

    private V1CamelMailBindingModel createModel() {
        V1CamelMailBindingModel model = new V1CamelMailBindingModel();
        model.setSecure(SECURE);
        model.setHost(HOST);

        CamelMailConsumerBindingModel consumer = new V1CamelMailConsumerBindingModel()
            .setAccountType(ACCOUNT_TYPE)
            .setFolderName(FOLDER_NAME)
            .setFetchSize(FETCH_SIZE)
            .setUnseen(UNSEEN)
            .setDelete(DELETE)
            .setCopyTo(COPY_TO)
            .setDisconnect(DISCONNECT);
        model.setConsumer(consumer);

        return model;
    }

    private void assertConsumerModel(CamelMailConsumerBindingModel consumer) {
        assertEquals(ACCOUNT_TYPE, consumer.getProtocol());
        assertEquals(FOLDER_NAME, consumer.getFolderName());
        assertEquals(FETCH_SIZE, consumer.getFetchSize());
        assertEquals(UNSEEN, consumer.isUnseen());
        assertEquals(DELETE, consumer.isDelete());
        assertEquals(COPY_TO, consumer.getCopyTo());
        assertEquals(DISCONNECT, consumer.isDisconnect());
    }
}
