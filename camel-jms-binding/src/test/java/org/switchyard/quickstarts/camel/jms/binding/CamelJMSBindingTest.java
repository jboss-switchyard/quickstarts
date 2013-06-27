package org.switchyard.quickstarts.camel.jms.binding;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

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

@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML, 
        mixins = {CDIMixIn.class, HornetQMixIn.class}
)
@RunWith(SwitchYardRunner.class)
public class CamelJMSBindingTest {
    
    private static final String QUEUE_NAME = "GreetingServiceQueue";
    private SwitchYardTestKit _testKit;
    
    @Test
    public void sendTextMessageToJMSQueue() throws Exception {
        final String payload = "dummy payload";
        // replace existing implementation for testing purposes
        _testKit.removeService("GreetingService");
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
