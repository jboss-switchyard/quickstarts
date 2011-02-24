package org.switchyard.quickstarts.m1app;

import javax.inject.Inject;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service(OrderService.class)
public class OrderServiceBean implements OrderService {
    
    @Inject @Reference
    private InventoryService _inventory;
    
    @Override
    public OrderAck submitOrder(Order order) {
        // Create an order ack
        OrderAck orderAck = new OrderAck().setOrderId(order.getOrderId());
        // Check the inventory
        try {
            Item orderItem = _inventory.lookupItem(order.getItemId());
            // Check quantity on hand and generate the ack
            if (orderItem.getQuantity() >= order.getQuantity()) {
                orderAck.setAccepted(true).setStatus("Order Accepted");
            } else {
                orderAck.setAccepted(false).setStatus("Insufficient Quantity");
            }
        } catch (ItemNotFoundException infEx) {
            orderAck.setAccepted(false).setStatus("Item Not Available");
        }
        return orderAck;
    }

}
