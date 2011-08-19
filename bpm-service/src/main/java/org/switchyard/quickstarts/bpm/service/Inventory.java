package org.switchyard.quickstarts.bpm.service;

import org.switchyard.quickstarts.bpm.service.data.Order;

public interface Inventory {
    
    /**
     * Verifies that there is inventory on hand to satisfy 
     * the item quantity requested in an order.
     * @param order the order
     * @return true if there is sufficient quantity, false otherwise
     */
    boolean checkAvailability(Order order);
    
}
