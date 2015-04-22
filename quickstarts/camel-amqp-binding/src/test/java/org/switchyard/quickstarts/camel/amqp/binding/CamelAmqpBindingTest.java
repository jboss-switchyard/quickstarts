/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.camel.amqp.binding;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.amqp.AMQPMixIn;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class, AMQPMixIn.class })
@RunWith(SwitchYardRunner.class)
public class CamelAmqpBindingTest {

    private final static String PAYLOAD = "Hola Mundo!";

    private SwitchYardTestKit _testKit;
    private AMQPMixIn _mixin;

    private Connection _connection;

    @Before
    public void setUp() throws Exception {
        _connection = _mixin.getConnectionFactory().createConnection();
        _connection.start();
    }

    @Test
    public void sendTextMessageToAmqpQueue() throws Exception {
        //replace existing implementation for testing purposes
        _testKit.removeService("GreetingService");
        final MockHandler greetingService = _testKit.registerInOnlyService("GreetingService");

        sentTextToQueue(PAYLOAD);
        greetingService.waitForOKMessage();

        final LinkedBlockingQueue<Exchange> receivedMessages = greetingService.getMessages();
        assertThat(receivedMessages, is(notNullValue()));
        final Exchange receivedExchange = receivedMessages.iterator().next();
        assertThat(receivedExchange.getMessage().getContent(String.class), is(equalTo(PAYLOAD)));
    }

    private void sentTextToQueue(String payload) throws Exception {
        Session session = _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = _mixin.getDestination(AMQPMixIn.DEFAULT_QUEUE_JNDI_LOCATION);

        MessageProducer messageProducer = session.createProducer(destination);

        TextMessage message = session.createTextMessage(payload);
        messageProducer.send(message);

        session.close();
    }

    @After
    public void shutDown() throws Exception {
        if (_connection != null) {
            _connection.close();
        }
    }

}
