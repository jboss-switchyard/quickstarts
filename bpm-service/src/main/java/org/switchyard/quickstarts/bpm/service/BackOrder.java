package org.switchyard.quickstarts.bpm.service;

import org.switchyard.quickstarts.bpm.service.data.Order;
import org.switchyard.quickstarts.bpm.service.data.OrderAck;

public interface BackOrder {
    
    /**
     * Place a hold on an order and stick it in the back order queue.
     * @param order the order to place on hold
     * @return an order ack indicating the order is not accepted
     */
    OrderAck hold(Order order);
    
}
