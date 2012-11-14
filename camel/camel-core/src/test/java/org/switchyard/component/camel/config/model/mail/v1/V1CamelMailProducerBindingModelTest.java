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
import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelMailBindingModel} with {@link V1CamelMailProducerBindingModel} set.
 */
public class V1CamelMailProducerBindingModelTest extends V1BaseCamelModelTest<V1CamelMailBindingModel> {

    private static final String CAMEL_XML = "switchyard-mail-binding-producer-beans.xml";

    private static final String HOST = "rider";
    private static final Boolean SECURE = true;
    private static final String SUBJECT = "Desert ride";
    private static final String FROM = "rider@camel";
    private static final String TO = "camel@rider";
    private static final String CC = "mule@rider";
    private static final String BCC = "rider@mule";
    private static final String REPLY_TO = "camel@camel";

    private static final String CAMEL_URI = UnsafeUriCharactersEncoder.encode(
        "smtps://rider?subject=Desert ride&from=rider@camel&to=camel@rider" +
        "&CC=mule@rider&BCC=rider@mule&replyTo=camel@camel"
    );

    @Test
    public void testConfigOverride() {
        // set a value on an existing config element
        V1CamelMailBindingModel bindingModel = createModel();
        assertEquals(SUBJECT, bindingModel.getProducer().getSubject());
        bindingModel.getProducer().setSubject(FROM);
        assertEquals(FROM, bindingModel.getProducer().getSubject());
    }

    @Test
    public void testReadConfig() throws Exception {
        final V1CamelMailBindingModel bindingModel = getFirstCamelReferenceBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        validateModel.assertValid();
        assertTrue(validateModel.isValid());
        //Camel
        assertEquals(HOST, bindingModel.getHost());
        assertEquals(SECURE, bindingModel.isSecure());
        //Camel Mail Producer
        assertEquals(SUBJECT, bindingModel.getProducer().getSubject());
        assertEquals(FROM, bindingModel.getProducer().getFrom());
        assertEquals(TO, bindingModel.getProducer().getTo());
        assertEquals(CC, bindingModel.getProducer().getCC());
        assertEquals(BCC, bindingModel.getProducer().getBCC());
        assertEquals(REPLY_TO, bindingModel.getProducer().getReplyTo());
        assertEquals(CAMEL_URI, bindingModel.getComponentURI().toString());
    }

    /**
     * This test fails because of namespace prefix 
     * 
     */
    @Test
    public void testWriteConfig() throws Exception {
        String refXml = getFirstCamelReferenceBinding(CAMEL_XML).toString();
        String newXml = createModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar()); 
    }

    @Test
    public void testComponentURI() throws Exception {
        V1CamelMailBindingModel bindingModel = getFirstCamelReferenceBinding(CAMEL_XML);
        assertEquals(CAMEL_URI.toString(), bindingModel.getComponentURI().toString());
    }

    @Test
    public void testCamelEndpoint() throws Exception {
        V1CamelMailBindingModel model = getFirstCamelReferenceBinding(CAMEL_XML);
        MailEndpoint endpoint = getEndpoint(model, MailEndpoint.class);

        assertEquals(SUBJECT, endpoint.getConfiguration().getSubject());
        assertEquals(FROM, endpoint.getConfiguration().getFrom());
        assertEquals(REPLY_TO, endpoint.getConfiguration().getReplyTo());
    }

    private V1CamelMailBindingModel createModel() {
        V1CamelMailBindingModel model = new V1CamelMailBindingModel()
           .setSecure(SECURE)
           .setHost(HOST);

        V1CamelMailProducerBindingModel producer = new V1CamelMailProducerBindingModel()
            .setSubject(SUBJECT)
            .setFrom(FROM)
            .setTo(TO)
            .setCC(CC)
            .setBCC(BCC)
            .setReplyTo(REPLY_TO);
        model.setProducer(producer);

        return model;
    }

}
