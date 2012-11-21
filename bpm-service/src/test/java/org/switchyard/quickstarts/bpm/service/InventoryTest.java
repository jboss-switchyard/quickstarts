package org.switchyard.quickstarts.bpm.service;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.quickstarts.bpm.service.data.Order;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins=CDIMixIn.class)
public class InventoryTest {
    
    @ServiceOperation("Inventory.checkAvailability")
    private Invoker service;
    
    @Test
    public void weGotIt() {
        Order order = new Order();
        order.setItemId("cowbell");
        order.setQuantity(50);
        
        Assert.assertTrue(
                service.sendInOut(order)
                    .getContent(Boolean.class));
    }
    
    @Test
    public void weDontGotIt() {
        Order order = new Order();
        order.setItemId("cowbell");
        order.setQuantity(500);
        
        Assert.assertFalse(
                service.sendInOut(order)
                    .getContent(Boolean.class));
    }
}