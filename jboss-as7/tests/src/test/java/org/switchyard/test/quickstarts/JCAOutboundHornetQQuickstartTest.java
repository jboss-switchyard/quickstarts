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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

@RunWith(Arquillian.class)
public class JCAOutboundHornetQQuickstartTest {

    private static final String ORDER_QUEUE = "OrderQueue";
    private static final String SHIPPING_QUEUE = "ShippingQueue";
    private static final String FILLING_STOCK_QUEUE = "FillingStockQueue";
    
    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws IOException {
        ResourceDeployer.addQueue(ORDER_QUEUE);
        ResourceDeployer.addQueue(SHIPPING_QUEUE);
        ResourceDeployer.addQueue(FILLING_STOCK_QUEUE);
        ResourceDeployer.addPropertiesUser();
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-jca-outbound-hornetq");
    }

    @Test
    public void testDeployment() throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(ResourceDeployer.USER)
                                    .setPassword(ResourceDeployer.PASSWD);
        hqMixIn.initialize();
        String[] orders = {"BREAD", "PIZZA", "JAM", "POTATO", "MILK", "JAM"};

        try {
            Session session = hqMixIn.getJMSSession();
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(ORDER_QUEUE));
            for (String order : orders) {
                BytesMessage message = session.createBytesMessage();
                message.writeBytes(order.getBytes());
                producer.send(message);
            }
            session = hqMixIn.createJMSSession();

            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(SHIPPING_QUEUE));
            List<String> expectedShippingOrders = new ArrayList<String>(Arrays.asList("BREAD", "JAM", "MILK", "JAM"));
            Message msg = null;
            while ((msg = consumer.receive(1000)) != null) {
                Assert.assertTrue(expectedShippingOrders.remove(hqMixIn.readStringFromJMSMessage(msg)));
            }
            Assert.assertEquals(0, expectedShippingOrders.size());

            consumer = session.createConsumer(HornetQMixIn.getJMSQueue(FILLING_STOCK_QUEUE));
            List<String> expectedFillingStockOrders = new ArrayList<String>(Arrays.asList("PIZZA", "POTATO"));
            while ((msg = consumer.receive(1000)) != null) {
                Assert.assertTrue(expectedFillingStockOrders.remove(hqMixIn.readStringFromJMSMessage(msg)));
            }
            Assert.assertEquals(0, expectedFillingStockOrders.size());
        } finally {
            hqMixIn.uninitialize();
            ResourceDeployer.removeQueue(ORDER_QUEUE);
            ResourceDeployer.removeQueue(SHIPPING_QUEUE);
            ResourceDeployer.removeQueue(FILLING_STOCK_QUEUE);
        }
    }

}
