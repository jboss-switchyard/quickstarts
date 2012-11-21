package org.switchyard.quickstarts.bpm.service;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.quickstarts.bpm.service.BackOrderBean;
import org.switchyard.quickstarts.bpm.service.data.Order;
import org.switchyard.quickstarts.bpm.service.data.OrderAck;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins=CDIMixIn.class)
public class BackOrderTest {
    
    @ServiceOperation("BackOrder.hold")
    private Invoker service;
    
    @Test
    public void sendMessage() {
        Order order = new Order();
        order.setOrderId("SHIPIT"); 
        OrderAck ack = service.sendInOut(order).getContent(OrderAck.class);
        
        Assert.assertFalse(ack.isAccepted());
        Assert.assertEquals(BackOrderBean.HOLD_STATUS, ack.getStatus());
    }
}