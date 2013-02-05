/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.quickstarts.jca.outbound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;

/**
 * Functional test for a SwitchYard Service which has reference bindings to a HornetQ
 * queue via JCA outbound.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {CDIMixIn.class, HornetQMixIn.class, JCAMixIn.class}
)
public class JCAOutboundBindingTest {
    private static final String ORDER_QUEUE = "OrderQueue";
    private static final String SHIPPING_QUEUE = "ShippingQueue";
    private static final String FILLING_STOCK_QUEUE = "FillingStockQueue";
    
    private HornetQMixIn _hqMixIn;
    private JCAMixIn _jcaMixIn;
    
    @BeforeDeploy
    public void before() {
        ResourceAdapterConfig ra = new ResourceAdapterConfig(ResourceAdapterConfig.ResourceAdapterType.HORNETQ);
        _jcaMixIn.deployResourceAdapters(ra);
    }
    /**
     * Triggers the 'OrderService' by sending a HornetQ Message to the 'OrderQueue'
     */
    @Test
    public void testOrderService() throws Exception {
        String[] orders = {"BREAD", "PIZZA", "JAM", "POTATO", "MILK", "JAM"};
        
        Session session = _hqMixIn.getJMSSession();
        final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(ORDER_QUEUE));
        for (String order : orders ) {
            final TextMessage message = _hqMixIn.getJMSSession().createTextMessage();
            message.setText(order);
            producer.send(message);
        }
        session.close();
        
        Thread.sleep(1000);
        
        session = _hqMixIn.createJMSSession();
        MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(SHIPPING_QUEUE));
        List<String> expectedShippingOrders = new ArrayList<String>(Arrays.asList("BREAD", "JAM", "MILK", "JAM"));
        Message msg = null;
        while ((msg = consumer.receive(1000)) != null) {
            Assert.assertTrue(expectedShippingOrders.remove(_hqMixIn.readStringFromJMSMessage(msg)));
        }
        Assert.assertEquals(0, expectedShippingOrders.size());
        
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(FILLING_STOCK_QUEUE));
        List<String> expectedFillingStockOrders = new ArrayList<String>(Arrays.asList("PIZZA", "POTATO"));
        while ((msg = consumer.receive(1000)) != null) {
            Assert.assertTrue(expectedFillingStockOrders.remove(_hqMixIn.readStringFromJMSMessage(msg)));
        }
        Assert.assertEquals(0, expectedFillingStockOrders.size());
    }
}
