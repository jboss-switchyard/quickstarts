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
