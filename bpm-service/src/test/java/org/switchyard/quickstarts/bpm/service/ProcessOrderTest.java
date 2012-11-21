package org.switchyard.quickstarts.bpm.service;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.quickstarts.bpm.service.data.Order;
import org.switchyard.quickstarts.bpm.service.data.OrderAck;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins=CDIMixIn.class, config=SwitchYardTestCaseConfig.SWITCHYARD_XML)
public class ProcessOrderTest {
    
    @ServiceOperation("ProcessOrder.submitOrder")
    private Invoker service;
    
    @Test
    public void orderShipped() {
        Order order = new Order();
        order.setItemId("cowbell");
        order.setOrderId("SHIPIT");
        order.setQuantity(50);

        OrderAck ack = service.sendInOut(order).getContent(OrderAck.class);
        
        Assert.assertTrue(ack.isAccepted());
        System.out.println(ack.getStatus());
    }
    
    @Test
    public void orderOnHold() {
        Order order = new Order();
        order.setItemId("cowbell");
        order.setOrderId("FEVER");
        order.setQuantity(500);

        OrderAck ack = service.sendInOut(order).getContent(OrderAck.class);
        
        Assert.assertFalse(ack.isAccepted());
        System.out.println(ack.getStatus());
    }
}