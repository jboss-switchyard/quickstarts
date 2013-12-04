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
package org.switchyard.quickstarts.rest.binding;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An order for OrderService.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "order")
public class Order {

    @XmlElement(name = "orderId", required = true)
    private Integer _orderId;
    @XmlElement(name = "orderItem", required = false)
    private List<OrderItem> _items;

    public Order() {
    }

    public Order(Integer orderId) {
        this(orderId, new ArrayList<OrderItem>());
    }

    public Order(Integer orderId, List<OrderItem> items) {
        _orderId = orderId;
        _items = items;
    }

    public Integer getOrderId() {
        return _orderId;
    }

    public void setOrderId(Integer orderId) {
        _orderId = orderId;
    }

    public List<OrderItem> getItems() {
        return _items;
    }

    public void setItems(List<OrderItem> items) {
        _items = items;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{OrderId: ");
        builder.append(_orderId);
        builder.append("\nItems:");
        for (OrderItem item : _items) {
            builder.append(item);
            builder.append(",");
        }
        builder.append("\n");
        builder.append("}");
        return builder.toString();
    }
}
