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
        return ArquillianUtil.createJarQSDeployment("switchyard-jca-outbound-hornetq");
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
