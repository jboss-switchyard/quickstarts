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

package org.switchyard.quickstarts.transform.json;

public class OrderAck {
    
    private String _orderId;
    private boolean _accepted;
    private String _status;
    
    public String getOrderId() {
        return _orderId;
    }
    
    public boolean isAccepted() {
        return _accepted;
    }
    
    public String getStatus() {
        return _status;
    }
    
    public OrderAck setOrderId(String orderId) {
        _orderId = orderId;
        return this;
    }
    
    public OrderAck setStatus(String status) {
        _status = status;
        return this;
    }
    
    public OrderAck setAccepted(boolean accepted) {
        _accepted = accepted;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OrderAck)) {
            return false;
        }
        
        OrderAck orderAck = (OrderAck)obj;
        return (orderAck.isAccepted() == isAccepted()) 
            && (orderAck.getStatus().equals(getStatus())
            && (orderAck.getOrderId().equals(getOrderId())));
    }
}
