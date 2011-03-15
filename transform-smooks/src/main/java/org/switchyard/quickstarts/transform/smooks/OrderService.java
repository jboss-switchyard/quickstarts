package org.switchyard.quickstarts.transform.smooks;

public interface OrderService {
    
    OrderAck submitOrder(Order order);
    
}
