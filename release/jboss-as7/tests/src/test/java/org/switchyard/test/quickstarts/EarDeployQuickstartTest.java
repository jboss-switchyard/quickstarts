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

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

@RunWith(Arquillian.class)
public class EarDeployQuickstartTest {

    private static final String ORDER_QUEUE_NAME = "EAROrderRequestQueue";
    private static final String ORDERACK_QUEUE_NAME = "EAROrderReplyQueue";

    @Deployment(testable = false)
    public static EnterpriseArchive createDeployment() throws IOException {
        return ArquillianUtil.createEarQSDeployment("switchyard-ear-deployment-ear-assembly");
    }

    @Test
    public void testDeployment() throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(ResourceDeployer.USER)
                                    .setPassword(ResourceDeployer.PASSWD);
        hqMixIn.initialize();

        try {
            Session session = hqMixIn.getJMSSession();
            // send the request
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(ORDER_QUEUE_NAME));
            Message message = hqMixIn.createJMSMessage(REQUEST);
            producer.send(message);
            
            // wait for a reply
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(ORDERACK_QUEUE_NAME));
            TextMessage reply = (TextMessage)consumer.receive(3000);
            Assert.assertNotNull("No reply message received from order-consumer", reply);
            Assert.assertTrue("Received message is not an orderAck", reply.getText().contains("orderAck"));
        } finally {
            hqMixIn.uninitialize();
        }
    }

    private static final String REQUEST = 
            "<orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:multiapp:1.0\">"
            + "<order>"
                + "<orderId>PO-19838-XYZ</orderId>"
                + "<itemId>BUTTER</itemId>"
                + "<quantity>200</quantity>"
            + "</order>"
           + "</orders:submitOrder>";

}
