package org.switchyard.quickstarts.camel.jms.binding;

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
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.test.MockHandler;
import org.switchyard.test.MockInitialContextFactory;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.CDIMixIn;

/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

@SwitchYardTestCaseConfig(config = SwitchYardTestCaseConfig.SWITCHYARD_XML, mixins = CDIMixIn.class)
@RunWith(SwitchYardRunner.class)
public class CamelJMSBindingTest {
    
    private static final String QUEUE_NAME = "GreetingServiceQueue";
    private static JMSServerManagerImpl jmsServer;
    private SwitchYardTestKit _testKit;
    
    @BeforeClass
    public static void setupHornetQServer() throws Exception {
        MockInitialContextFactory.install();
        
        final FileConfiguration fileConfiguration = new FileConfiguration();
        final URL hornetqBeans = CamelJMSBindingTest.class.getResource("hornetq-configuration.xml");
        fileConfiguration.setConfigurationUrl(hornetqBeans.toURI().toString());
        fileConfiguration.start();
        
        jmsServer = new JMSServerManagerImpl(new HornetQServerImpl(fileConfiguration), 
                "org/switchyard/quickstarts/camel/jms/binding/hornetq-jms.xml");
        jmsServer.start();
        jmsServer.activated();
    }
    
    @AfterClass
    public static void stopHornetQServer() throws Exception {
        if (jmsServer != null) {
	        jmsServer.stop();
        }
    }
    
    @Test
    public void sendTextMessageToJMSQueue() throws Exception {
        final String payload = "dummy payload";
        final MockHandler greetingService = _testKit.registerInOnlyService("GreetingService");
        
        sendTextToQueue(payload, QUEUE_NAME);
        // Allow for the JMS Message to be processed.
        Thread.sleep(3000);
        
        final LinkedBlockingQueue<Exchange> recievedMessages = greetingService.getMessages();
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
            if (producer != null) {
	            producer.close();
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
}
