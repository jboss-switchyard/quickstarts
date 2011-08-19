package org.switchyard.quickstarts.bpm.service;

import org.switchyard.component.bean.Service;
import org.switchyard.quickstarts.bpm.service.data.Order;
import org.switchyard.quickstarts.bpm.service.data.OrderAck;

@Service(BackOrder.class) 
public class BackOrderBean implements org.switchyard.quickstarts.bpm.service.BackOrder {

    public static final String HOLD_STATUS = 
            "Insufficient quantity on hand - order has been placed on hold.";

    @Override
    public OrderAck hold(Order order) {
        OrderAck ack = new OrderAck();
        ack.setAccepted(false);
        ack.setOrderId(order.getOrderId());
        ack.setStatus(HOLD_STATUS);
        
        return ack;
    }

}
