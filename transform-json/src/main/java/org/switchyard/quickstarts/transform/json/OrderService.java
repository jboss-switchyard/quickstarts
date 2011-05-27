package org.switchyard.quickstarts.transform.json;

public interface OrderService {
    
    OrderAck submitOrder(Order order);
    
}
