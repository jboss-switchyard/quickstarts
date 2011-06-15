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
package org.switchyard.component.camel.deploy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.hornetq.core.config.impl.FileConfiguration;
import org.hornetq.core.server.impl.HornetQServerImpl;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.test.MockHandler;
import org.switchyard.test.MockInitialContextFactory;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

/**
 * Test using Camel's JMS Component in SwitchYard.
 * 
 * @author Daniel Bevenius
 * 
 */
@SwitchYardTestCaseConfig(config = "switchyard-jms-test.xml", mixins = CDIMixIn.class)
public class CamelJMSTest extends SwitchYardTestCase {
    
    private static JMSServerManagerImpl jmsServer;
    
    @BeforeClass
    public static void setupHornetQServer() throws Exception {
        MockInitialContextFactory.install();
        
        final FileConfiguration fileConfiguration = new FileConfiguration();
        final URL hornetqBeans = CamelJMSTest.class.getResource("hornetq-configuration.xml");
        fileConfiguration.setConfigurationUrl(hornetqBeans.toURI().toString());
        fileConfiguration.start();
        
        jmsServer = new JMSServerManagerImpl(new HornetQServerImpl(fileConfiguration), 
                "org/switchyard/component/camel/deploy/hornetq-jms.xml");
        jmsServer.start();
        jmsServer.activated();
    }
    
    @Test
    public void sendOneWayTextMessageToJMSQueue() throws Exception {
        final String payload = "dummy payload";
        final MockHandler simpleCamelService = registerInOnlyService("SimpleCamelService");
        
        sendTextToQueue(payload, "testQueue");
        // Allow for the JMS Message to be processed.
        Thread.sleep(3000);
        
        final LinkedBlockingQueue<Exchange> recievedMessages = simpleCamelService.getMessages();
        assertThat(recievedMessages, is(notNullValue()));
        final Exchange recievedExchange = recievedMessages.iterator().next();
        assertThat(recievedExchange.getMessage().getContent(String.class), is(equalTo(payload)));
    }
    
    private void sendTextToQueue(final String text, final String queueName) throws Exception {
        InitialContext initialContext = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            initialContext = new InitialContext();
            final Queue testQueue = (Queue) initialContext.lookup(queueName);
            final ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(testQueue);
            producer.send(session.createTextMessage(text));
        } finally {
            producer.close();
            session.close();
            connection.close();
            initialContext.close();
        }
    }
    
    @AfterClass
    public static void stopHornetQServer() throws Exception {
        jmsServer.stop();
    }
    
}
