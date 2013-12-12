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
        System.out.println("|--- Validating OrderAck object:[OrderID=" + orderAck.getOrderId() + ", accepted=" + orderAck.isAccepted() + ", status=" + orderAck.getStatus()
            + "] ---|");
        return BaseValidator.validResult();
    }

}
