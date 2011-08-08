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

package org.switchyard.quickstarts.demos.orders;

public class Order {

    private String _orderId;
    private String _itemId;
    private int _quantity;
    
    public Order setItemId(String itemId) {
        _itemId = itemId;
        return this;
    }
    
    public Order setOrderId(String orderId) {
        _orderId = orderId;
        return this;
    }

    public Order setQuantity(int quantity) {
        _quantity = quantity;
        return this;
    }

    public String getOrderId() {
        return _orderId;
    }
    
    public String getItemId() {
        return _itemId;
    }
    
    public int getQuantity() {
        return _quantity;
    }
}
