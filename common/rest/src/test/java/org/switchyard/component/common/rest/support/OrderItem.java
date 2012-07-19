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

package org.switchyard.component.common.rest.support;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Item for an Order.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "orderItem")
public class OrderItem {

    @XmlElement(name = "item", required = true)
    private Item _item;
    @XmlElement(name = "quantity", required = true)
    private Integer _quantity;

    public OrderItem() {
    }

    public OrderItem(Item item) {
        _item = item;
        _quantity = 1;
    }

    public OrderItem(Item item, Integer quantity) {
        _item = item;
        _quantity = quantity;
    }

    public Item getItem() {
        return _item;
    }

    public void setItem(Item item) {
        _item = item;
    }

    public Integer getQuantity() {
        return _quantity;
    }

    public void setQuantity(Integer quantity) {
        _quantity = quantity;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{Item: ");
        builder.append(_item);
        builder.append(", quantity:");
        builder.append(_quantity);
        builder.append("}");
        return builder.toString();
    }
}
