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
import org.switchyard.component.camel.config.test.v1.V1BaseCamelReferenceBindingModelTest;
import org.switchyard.component.camel.mail.model.CamelMailNamespace;

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
        V1CamelMailBindingModel model = new V1CamelMailBindingModel(CamelMailNamespace.V_1_0.uri()) {
            public boolean isReferenceBinding() {
                return true;
            }
        };
        model.setSecure(SECURE)
           .setHost(HOST);

        V1CamelMailProducerBindingModel producer = new V1CamelMailProducerBindingModel(CamelMailNamespace.V_1_0.uri())
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
