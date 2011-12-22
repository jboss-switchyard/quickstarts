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
package org.switchyard.quickstarts.transform.json;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.hornetq.internal.HornetQUtil;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HornetQMixIn;

/**
 * Functional test for a SwitchYard Service which has a service binding to a HornetQ
 * queue.
 * 
 * @author Daniel Bevenius
 * @author <a href="tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {CDIMixIn.class, HornetQMixIn.class},
        scanners = BeanSwitchYardScanner.class)
public class HornetQBindingTest {
    
    private SwitchYardTestKit _testKit;
    private HornetQMixIn _mixIn;
    
    /**
     * Triggers the 'OrderService' by sending a HornetQ Mesage to the 'OrderServiceQueue'
     * and retrieve the result by receiving a HornetQ Message from the 'StoreResponseQueue'
     */
    @Test
    public void triggerOrderService() throws Exception {
        final String payload = _testKit.readResourceString("json/order.json");
        
        final ClientProducer producer = _mixIn.getClientSession().createProducer("jms.queue.OrderServiceQueue");
        final ClientMessage message = _mixIn.createMessage(payload);
        producer.send(message);
        HornetQUtil.closeClientProducer(producer);
        
        final ClientConsumer consumer = _mixIn.getClientSession().createConsumer("jms.queue.StoreResponseQueue");
        final ClientMessage received = consumer.receive(3000);
        assertThat(received, not(equalTo(null)));
        String expected = _testKit.readResourceString("json/orderAck.json");
        assertThat(new String(HornetQUtil.readBytes(received)), is(equalTo(expected)));
    }
    
    @Before
    public void getHornetQMixIn() {
        _mixIn = _testKit.getMixIn(HornetQMixIn.class);
    }
    
}
