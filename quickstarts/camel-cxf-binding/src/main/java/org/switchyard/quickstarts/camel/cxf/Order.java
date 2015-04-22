/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
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
package org.switchyard.quickstarts.camel.cxf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "order", namespace = "urn:switchyard-quickstart:camel-cxf:2.0")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order", namespace = "urn:switchyard-quickstart:camel-cxf:2.0", propOrder = {
    "item",
    "quantity"
})
public class Order {

    @XmlElement(name = "item", namespace = "")
    private String item;
    @XmlElement(name = "quantity", namespace = "")
    private int quantity;

    /**
     * 
     * @return
     *     returns String
     */
    public String getItem() {
        return this.item;
    }

    /**
     * 
     * @param item
     *     the value for the item property
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * 
     * @return
     *     returns int
     */
    public int getQuantity() {
        return this.quantity;
    }

    /**
     * 
     * @param quantity
     *     the value for the quantity property
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
