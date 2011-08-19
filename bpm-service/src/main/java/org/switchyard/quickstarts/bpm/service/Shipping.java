package org.switchyard.quickstarts.bpm.service;

import org.switchyard.quickstarts.bpm.service.data.Order;
import org.switchyard.quickstarts.bpm.service.data.OrderAck;

public interface Shipping {
    
    /**
     * Ship an order and generate an order ack.
     * @param order the order to ship
     * @return an order ack
     */
    OrderAck ship(Order order);
    
}
