package org.switchyard.quickstarts.bpm.service;

import org.switchyard.quickstarts.bpm.service.data.Order;
import org.switchyard.quickstarts.bpm.service.data.OrderAck;

public interface ProcessOrder {
    OrderAck submitOrder(Order order);
}
