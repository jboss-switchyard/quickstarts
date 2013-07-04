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
