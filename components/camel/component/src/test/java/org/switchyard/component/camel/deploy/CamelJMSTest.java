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
package org.switchyard.component.camel.deploy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

/**
 * Test using Camel's JMS Component in SwitchYard.
 *
 * @author Daniel Bevenius
 */
@SwitchYardTestCaseConfig(config = "switchyard-jms-test.xml", mixins = {CDIMixIn.class, HornetQMixIn.class})
@RunWith(SwitchYardRunner.class)
public class CamelJMSTest {

    private ServiceDomain _domain;

    private SwitchYardTestKit _testKit;

    @Test
    public void sendOneWayTextMessageToJMSQueue() throws Exception {
        sendAndAssertOneMessage();
    }

    @Test
    public void stopAndStartCamelActivator() throws Exception {
        sendAndAssertOneMessage();
        stopCamelActivator();

        Thread.sleep(2000);

        startCamelActivator();
        sendAndAssertOneMessage();
    }

    private void stopCamelActivator() throws Exception {
        CamelContext context = (CamelContext) _domain.getProperty(SwitchYardCamelContext.CAMEL_CONTEXT_PROPERTY);
        context.suspend();
    }

    private void startCamelActivator() throws Exception {
        CamelContext context = (CamelContext) _domain.getProperty(SwitchYardCamelContext.CAMEL_CONTEXT_PROPERTY);
        context.resume();
    }

    private void sendAndAssertOneMessage() throws Exception, InterruptedException {
        final String payload = "dummy payload";
        // remove the currently registered service for SimpleCamelService
        _testKit.removeService("SimpleCamelService");
        final MockHandler simpleCamelService = _testKit.registerInOnlyService("SimpleCamelService");

        sendTextToQueue(payload, "testQueue");
        // Allow for the JMS Message to be processed.
        Thread.sleep(3000);

        final LinkedBlockingQueue<Exchange> recievedMessages = simpleCamelService.getMessages();
        assertThat(recievedMessages, is(notNullValue()));
        final Exchange recievedExchange = recievedMessages.iterator().next();
        assertThat(recievedExchange.getMessage().getContent(String.class), is(equalTo(payload)));
    }

    private void sendTextToQueue(final String text, final String destinationName) throws Exception {
        InitialContext initialContext = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            initialContext = new InitialContext();
            final ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            final Destination destination = (Destination) initialContext.lookup(destinationName);
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
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
