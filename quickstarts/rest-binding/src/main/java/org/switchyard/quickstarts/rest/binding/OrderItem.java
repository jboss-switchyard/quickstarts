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
