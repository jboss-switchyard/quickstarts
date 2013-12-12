/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
