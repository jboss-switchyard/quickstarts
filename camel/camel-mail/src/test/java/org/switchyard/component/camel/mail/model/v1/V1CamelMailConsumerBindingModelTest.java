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
import org.switchyard.component.camel.mail.model.CamelMailConsumerBindingModel;
import org.switchyard.component.camel.mail.model.v1.V1CamelMailConsumerBindingModel.AccountType;

/**
 * Test for {@link V1CamelMailBindingModel} with {@link V1CamelMailConsumerBindingModel} set.
 */
public class V1CamelMailConsumerBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelMailBindingModel, MailEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-mail-binding-consumer-beans.xml";

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

    public V1CamelMailConsumerBindingModelTest() {
        super(MailEndpoint.class, CAMEL_XML);
    }

    @Override
    protected V1CamelMailBindingModel createTestModel() {
        V1CamelMailBindingModel model = new V1CamelMailBindingModel() {
            @Override
            public boolean isReferenceBinding() {
                return false;
            }
        };

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

    @Override
    protected void createModelAssertions(V1CamelMailBindingModel model) {
        assertEquals(HOST, model.getHost());
        assertEquals(SECURE, model.isSecure());

        V1CamelMailConsumerBindingModel consumer = model.getConsumer();
        assertEquals(ACCOUNT_TYPE, consumer.getProtocol());
        assertEquals(FOLDER_NAME, consumer.getFolderName());
        assertEquals(FETCH_SIZE, consumer.getFetchSize());
        assertEquals(UNSEEN, consumer.isUnseen());
        assertEquals(DELETE, consumer.isDelete());
        assertEquals(COPY_TO, consumer.getCopyTo());
        assertEquals(DISCONNECT, consumer.isDisconnect());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }
}
