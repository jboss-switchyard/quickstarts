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

package org.switchyard.quickstarts.bean.service;

import javax.inject.Named;
import org.switchyard.annotations.Validator;
import org.switchyard.validate.ValidationResult;
import org.switchyard.validate.BaseValidator;

@Named("Validators")
public class Validators {

    /**
     * Validates Order object.
     * @param order Order object.
     * @return validation result
     */
    @Validator
    public ValidationResult validate(Order order) {
        System.out.println("|--- Validating Order object:[OrderID=" + order.getOrderId() + ", ItemID=" + order.getItemId() + ", quantity=" + order.getQuantity() + "] ---|");
        if (order.getQuantity() > 1000) {
            return BaseValidator.invalidResult("Too many order quantity: " + order.getQuantity());
        }
        return BaseValidator.validResult();
    }

    /**
     * Validates OrderAck object.
     * @param orderAck OrderAck object.
     * @return validation result
     */
    @Validator
    public ValidationResult validate(OrderAck orderAck) {
        System.out.println("|--- Validating OrderAck object:[OrderID=" + orderAck.getOrderId() + ", accepted=" + orderAck.isAccepted() + ", status=" + orderAck.getStatus() + "] ---|");
        return BaseValidator.validResult();
    }

}
