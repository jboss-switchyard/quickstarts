/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
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
package org.switchyard.karaf.test.quickstarts;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.test.SwitchYardTestKit;

public class BpelServiceJmsBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.bpel.jms.binding";
    private static String featureName = "switchyard-quickstart-bpel-jms-binding";

    private static final String AMQ_USER = "karaf";
    private static final String AMQ_PASSWD = "karaf";
    private static final String AMQ_BROKER_URL = "tcp://localhost:61616";

    private static final String REQUEST_QUEUE = "HelloRequestQueue";
    private static final String REPLY_QUEUE = "HelloReplyQueue";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Override
    @Test
    public void testDeployment() throws Exception {
        ConnectionFactory cf = new ActiveMQConnectionFactory(AMQ_USER, AMQ_PASSWD, AMQ_BROKER_URL);
        Connection conn = cf.createConnection();

        try {
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final MessageProducer producer = session.createProducer(session.createQueue(REQUEST_QUEUE));
            final MessageConsumer consumer = session.createConsumer(session.createQueue(REPLY_QUEUE));
            conn.start();

            producer.send(session.createObjectMessage(INPUT));

            Message message = consumer.receive(3000);
            String reply = ((TextMessage)message).getText();
            SwitchYardTestKit.compareXMLToString(reply, EXPECTED_REPLY);
        } finally {
            conn.close();
        }
    }

    private static final String INPUT = "<exam:sayHello xmlns:exam=\"http://www.jboss.org/bpel/examples\">"
            + "<exam:input>Tomo</exam:input>"
            + "</exam:sayHello>";

    private static final String EXPECTED_REPLY = "<sayHelloResponse xmlns=\"http://www.jboss.org/bpel/examples\">"
            + "<tns:result xmlns:tns=\"http://www.jboss.org/bpel/examples\">Hello Tomo</tns:result>"
            + "</sayHelloResponse>";

}
