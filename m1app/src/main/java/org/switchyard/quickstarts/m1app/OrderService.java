package org.switchyard.quickstarts.m1app;

public interface OrderService {
    
    OrderAck submitOrder(Order order);
    
}
