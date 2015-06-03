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
package org.switchyard.quickstarts.bpel.service.hello;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

/**
 * Functional test for a SwitchYard Service which has a BPEL service and camel-jms
 * binding to a HornetQ queue.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class, HornetQMixIn.class })
public class JmsBindingTest {

    private static final String REQUEST_NAME = "HelloRequestQueue";
    private static final String REPLY_NAME = "HelloReplyQueue";
    private static final String NAME = "Tomo";

    private SwitchYardTestKit _testKit;
    private HornetQMixIn _hqMixIn;

    /**
     * Send a message to HelloRequestQueue and receive from HelloResponseQueue.
     */
    @Test
    public void testHelloService() throws Exception {
        Session session = _hqMixIn.getJMSSession();
        MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(REQUEST_NAME));
        Message message = _hqMixIn.createJMSMessage(createPayload(NAME));
        producer.send(message);

        MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(REPLY_NAME));
        message = consumer.receive(3000);
        String reply = _hqMixIn.readStringFromJMSMessage(message);
        SwitchYardTestKit.compareXMLToString(reply, createExpectedReply(NAME));
    }

    @Before
    public void getHornetQMixIn() {
        _hqMixIn = _testKit.getMixIn(HornetQMixIn.class);
    }

    private static String createPayload(String name) {
        return "<exam:sayHello xmlns:exam=\"http://www.jboss.org/bpel/examples\">"
            + "<exam:input>" + name + "</exam:input>"
            + "</exam:sayHello>";
    }

    private static String createExpectedReply(String name) {
        return "<sayHelloResponse xmlns=\"http://www.jboss.org/bpel/examples\">"
            + "<tns:result xmlns:tns=\"http://www.jboss.org/bpel/examples\">Hello " + name + "</tns:result>"
            + "</sayHelloResponse>";
    }
}
