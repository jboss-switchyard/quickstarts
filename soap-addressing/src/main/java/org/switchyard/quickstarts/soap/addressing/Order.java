/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.quickstarts.soap.addressing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "order", namespace = "urn:switchyard-quickstart:soap-addressing:1.0")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order", namespace = "urn:switchyard-quickstart:soap-addressing:1.0", propOrder = {
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
