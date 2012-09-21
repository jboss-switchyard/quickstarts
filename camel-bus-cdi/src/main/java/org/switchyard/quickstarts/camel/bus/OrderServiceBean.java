/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
 */
package org.switchyard.quickstarts.camel.bus;

import org.switchyard.component.bean.Service;

/**
 * Dummy implementation of order service.
 */
@Service(OrderService.class)
public class OrderServiceBean implements OrderService {

    private int _counter;

    @Override
    public OrderAck submitOrder(Order order) {
        // Create an order ack
        OrderAck orderAck = new OrderAck();
        orderAck.setStatus("Order Accepted");
        if ((++_counter % 2) == 0) {
            try {
                Thread.sleep(1000);
                orderAck.setStatus("Order Accepted, after delay");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        orderAck.setOrderId(order.getOrderId());
        orderAck.setAccepted(true);

        return orderAck;
    }

}
