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
package org.switchyard.quickstarts.bpm.service;

import org.switchyard.component.bean.Service;
import org.switchyard.quickstarts.bpm.service.data.Order;
import org.switchyard.quickstarts.bpm.service.data.OrderAck;

@Service(Shipping.class)
public class ShippingBean implements org.switchyard.quickstarts.bpm.service.Shipping {

    public static final String SHIPPED_STATUS =
        "Thanks for your order, it has been shipped!";

    @Override
    public OrderAck ship(Order order) {
        OrderAck ack = new OrderAck();
        ack.setAccepted(true);
        ack.setOrderId(order.getOrderId());
        ack.setStatus(SHIPPED_STATUS);

        return ack;
    }

}
