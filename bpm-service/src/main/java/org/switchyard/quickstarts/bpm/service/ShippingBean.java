package org.switchyard.quickstarts.bpm.service;

import org.switchyard.component.bean.Service;
import org.switchyard.quickstarts.bpm.service.data.Order;
import org.switchyard.quickstarts.bpm.service.data.OrderAck;

@Service(Shipping.class) 
public class ShippingBean implements org.switchyard.quickstarts.bpm.service.Shipping {
    
    public static final String SHIPPED_STATUS = 
            "Thanks for your order, it has been shipped!";

    @Override
    public OrderAck ship(Order order) {
        OrderAck ack = new OrderAck();
        ack.setAccepted(true);
        ack.setOrderId(order.getOrderId());
        ack.setStatus(SHIPPED_STATUS);
        
        return ack;
    }

}
