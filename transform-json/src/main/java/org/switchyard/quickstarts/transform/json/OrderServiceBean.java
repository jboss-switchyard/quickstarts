package org.switchyard.quickstarts.transform.json;

import org.switchyard.component.bean.Service;

@Service(OrderService.class)
public class OrderServiceBean implements OrderService {
    
    @Override
    public OrderAck submitOrder(Order order) {
        // Create an order ack
        return new OrderAck()
            .setOrderId(order.getOrderId())
            .setAccepted(true)
            .setStatus("Order Accepted");
    }

}
