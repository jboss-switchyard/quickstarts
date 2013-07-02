/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
        ResourceDeployer.addPropertiesUser();
        return ArquillianUtil.createEarQSDeployment("switchyard-quickstart-ear-deployment-assembly");
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
