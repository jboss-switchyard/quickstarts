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
package org.switchyard.quickstarts.bpel.service.hello;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HornetQMixIn;

/**
 * Functional test for a SwitchYard Service which has a BPEL service and camel-jms
 * binding to a HornetQ queue.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {CDIMixIn.class, HornetQMixIn.class},
        scanners = BeanSwitchYardScanner.class)
public class JmsBindingTest {
    
    private static final String REQUEST_NAME = "HelloRequestQueue";
    private static final String REPLY_NAME = "HelloReplyQueue";
    
    /**
     * Send a message to HelloRequestQueue and receive from HelloResponseQueue.
     */
    @Test
    public void testHelloService() throws Exception {
        InitialContext initialContext = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            initialContext = new InitialContext();
            final Queue requestQueue = (Queue) initialContext.lookup(REQUEST_NAME);
            final ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(requestQueue);
            producer.send(session.createTextMessage(createPayload("Tomo")));
            session.close();
            
            final Queue replyQueue = (Queue) initialContext.lookup(REPLY_NAME);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(replyQueue);
            TextMessage message = (TextMessage)consumer.receive(3000);
            
            Assert.assertNotNull(message);
            String response = message.getText();
            Assert.assertEquals(createExpectedReply("Tomo"), response);
        } finally {
            if (producer != null) {
                    producer.close();
            }
            if (consumer != null) {
                consumer.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
            if (initialContext != null) {
                initialContext.close();
            }
        }
    }
    
    private static String createPayload(String name) {
        return "<exam:sayHello xmlns:exam=\"http://www.jboss.org/bpel/examples\">"
                + "<exam:input>" + name + "</exam:input>"
                + "</exam:sayHello>";
    }

    private static String createExpectedReply(String name) {
        return "<sayHelloResponse xmlns=\"http://www.jboss.org/bpel/examples\">\n"
                + "  <tns:result xmlns:tns=\"http://www.jboss.org/bpel/examples\">Hello " + name + "</tns:result>\n"
                + "</sayHelloResponse>";
    }
}