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
package org.switchyard.component.test.mixins.activemq;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for {@link ActiveMQMixIn}.
 */
public class ActiveMQMixInTest {

    private static ActiveMQMixIn activeMQMixIn;

    @BeforeClass
    public static void setup() {
        activeMQMixIn = new ActiveMQMixIn();
        activeMQMixIn.initialize();
    }

    @AfterClass
    public static void tearDown() {
        activeMQMixIn.uninitialize();
    }

    @Test
    public void getClientSession() {
        final Session clientSession = activeMQMixIn.getSession();
        Assert.assertNotNull(clientSession);
    }

    @Test
    public void createQueue() throws JMSException {
        final Session session = activeMQMixIn.getSession();
        Queue queue = session.createQueue("foo");

        MessageProducer producer = session.createProducer(queue);
        producer.send(session.createTextMessage("bar"));
        producer.close();

        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage msg = (TextMessage) consumer.receive();
        consumer.close();

        Assert.assertEquals("bar", msg.getText());
        session.close();
    }

}
