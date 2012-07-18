/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
