package org.switchyard.quickstarts.bpm.service;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.quickstarts.bpm.service.ShippingBean;
import org.switchyard.quickstarts.bpm.service.data.Order;
import org.switchyard.quickstarts.bpm.service.data.OrderAck;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins=CDIMixIn.class)
public class ShippingTest {
    
    @ServiceOperation("Shipping.ship")
    private Invoker service;
    
    @Test
    public void testShip() {
        Order order = new Order();
        order.setOrderId("SHIPIT"); 
        OrderAck ack = service.sendInOut(order).getContent(OrderAck.class);
        
        Assert.assertTrue(ack.isAccepted());
        Assert.assertEquals(ShippingBean.SHIPPED_STATUS, ack.getStatus());
    }
}