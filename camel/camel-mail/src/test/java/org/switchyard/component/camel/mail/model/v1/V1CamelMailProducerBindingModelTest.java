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
import org.switchyard.component.camel.config.test.v1.V1BaseCamelReferenceBindingModelTest;

/**
 * Test for {@link V1CamelMailBindingModel} with {@link V1CamelMailProducerBindingModel} set.
 */
public class V1CamelMailProducerBindingModelTest extends V1BaseCamelReferenceBindingModelTest<V1CamelMailBindingModel, MailEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-mail-binding-producer-beans.xml";

    private static final String HOST = "rider";
    private static final Boolean SECURE = true;
    private static final String SUBJECT = "Desert ride";
    private static final String FROM = "rider@camel";
    private static final String TO = "camel@rider";
    private static final String CC = "mule@rider";
    private static final String BCC = "rider@mule";
    private static final String REPLY_TO = "camel@camel";

    private static final String CAMEL_URI = "smtps://rider?subject=Desert ride&" +
        "from=rider@camel&to=camel@rider&CC=mule@rider&BCC=rider@mule&replyTo=camel@camel";

    public V1CamelMailProducerBindingModelTest() {
        super(MailEndpoint.class, CAMEL_XML);
    }

    @Override
    protected void createModelAssertions(V1CamelMailBindingModel model) {
        assertEquals(HOST, model.getHost());
        assertEquals(SECURE, model.isSecure());
        assertEquals(SUBJECT, model.getProducer().getSubject());
        assertEquals(FROM, model.getProducer().getFrom());
        assertEquals(TO, model.getProducer().getTo());
        assertEquals(CC, model.getProducer().getCC());
        assertEquals(BCC, model.getProducer().getBCC());
        assertEquals(REPLY_TO, model.getProducer().getReplyTo());
    }

    @Override
    protected V1CamelMailBindingModel createTestModel() {
        V1CamelMailBindingModel model = new V1CamelMailBindingModel() {
            public boolean isReferenceBinding() {
                return true;
            }
        };
        model.setSecure(SECURE)
           .setHost(HOST);

        V1CamelMailProducerBindingModel producer = new V1CamelMailProducerBindingModel()
            .setSubject(SUBJECT)
            .setFrom(FROM)
            .setTo(TO)
            .setCC(CC)
            .setBCC(BCC)
            .setReplyTo(REPLY_TO);

        return model.setProducer(producer);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
