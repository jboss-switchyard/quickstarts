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
package org.switchyard.test.mixins.amqp;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.SwitchYardException;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for AMQMixIn
 * 
 * @author: <a href="mailto:eduardo.devera@gmail.com">Eduardo de Vera</a>
 */
@SwitchYardTestCaseConfig(mixins = AMQPMixIn.class)
@RunWith(SwitchYardRunner.class)
public class AMQPMixInTest {

    private AMQPMixIn _mixin;

    private Connection _connection;

    @Before
    public void setUp() throws Exception {
        _connection = _mixin.getConnectionFactory().createConnection();
        _connection.start();
    }

    @After
    public void tearDown() {
        try {
            _connection.close();
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }

    @Test
    public void testConnection() {
        assertThat(_connection, is(notNullValue()));
    }

    @Test
    public void testSendMessageToQueue() throws Exception {
        assertMessageSendAndReceive(AMQPMixIn.DEFAULT_QUEUE_JNDI_LOCATION, "Hello queued world!");
    }

    @Test
    public void testSendMessageToTopic() throws Exception {
        assertMessageSendAndReceive(AMQPMixIn.DEFAULT_TOPIC_JNDI_LOCATION, "Hello topical world!");
    }

    private void assertMessageSendAndReceive(String destinationName, String messageText) throws Exception {
        Session session = _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = (Destination) _mixin.getDestination(destinationName);

        MessageProducer messageProducer = session.createProducer(destination);
        MessageConsumer messageConsumer = session.createConsumer(destination);

        TextMessage message = session.createTextMessage(messageText);
        messageProducer.send(message);

        message = (TextMessage) messageConsumer.receive();
        assertThat(message.getText(), is(equalTo(messageText)));
    }
}
