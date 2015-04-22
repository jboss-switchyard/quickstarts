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
package org.switchyard.test.quickstarts;

import java.io.IOException;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Message;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

/**
 * A testcase for deploy & invoke the bpel-service/jms_binding quickstart on AS7.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@RunWith(Arquillian.class)
public class BpelServiceJmsBindingQuickstartTest {

    private static final String REQUEST_QUEUE = "HelloRequestQueue";
    private static final String REPLY_QUEUE = "HelloReplyQueue";
    
    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws IOException {
        ResourceDeployer.addQueue(REQUEST_QUEUE);
        ResourceDeployer.addQueue(REPLY_QUEUE);
        return ArquillianUtil.createJarQSDeployment("switchyard-bpel-jms-binding");
    }

    @Test
    public void testJmsBinding() throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(ResourceDeployer.USER)
                                    .setPassword(ResourceDeployer.PASSWD);
        hqMixIn.initialize();
        
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            session = hqMixIn.createJMSSession();
            producer = session.createProducer(HornetQMixIn.getJMSQueue(REQUEST_QUEUE));
            Message message = hqMixIn.createJMSMessage(INPUT);
            producer.send(message);

            consumer = session.createConsumer(HornetQMixIn.getJMSQueue(REPLY_QUEUE));
            message = consumer.receive(3000);
            String reply = hqMixIn.readStringFromJMSMessage(message);
            SwitchYardTestKit.compareXMLToString(reply, EXPECTED_REPLY);
        } finally {
            hqMixIn.uninitialize();
        }
    }

    @After
    public void undeploy() throws IOException {
        ResourceDeployer.removeQueue(REQUEST_QUEUE);
        ResourceDeployer.removeQueue(REPLY_QUEUE);
    }
    
    private static final String INPUT = "<exam:sayHello xmlns:exam=\"http://www.jboss.org/bpel/examples\">"
                + "<exam:input>Tomo</exam:input>"
                + "</exam:sayHello>";

    private static final String EXPECTED_REPLY = "<sayHelloResponse xmlns=\"http://www.jboss.org/bpel/examples\">"
                + "<tns:result xmlns:tns=\"http://www.jboss.org/bpel/examples\">Hello Tomo</tns:result>"
                + "</sayHelloResponse>";

}
