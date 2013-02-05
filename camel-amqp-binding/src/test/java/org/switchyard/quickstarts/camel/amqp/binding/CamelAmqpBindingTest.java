/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
        mixins = {CDIMixIn.class, AMQPMixIn.class}
)
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
