/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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

package org.switchyard.metadata;

import javax.xml.namespace.QName;

import org.switchyard.ExchangePattern;

/**
 * Default implementation of ServiceOperation for ExchangePattern.IN_ONLY 
 * operations.
 */
public class InOnlyOperation extends BaseServiceOperation {
    
    protected InOnlyOperation() {
        super(ExchangePattern.IN_OUT);
    }

    /**
     * Create a new instance of an InOnly operation with a default message name.
     * @param operationName the name of the operation
     */
    public InOnlyOperation(String operationName) {
        super(ExchangePattern.IN_ONLY, operationName, null, null, null);
    }
    
    /**
     * Create a new instance of an InOnly operation with a specific message name.
     * @param operationName the name of the operation
     * @param inputName the name of the input message
     */
    public InOnlyOperation(String operationName, QName inputName) {
        super(ExchangePattern.IN_ONLY, operationName, inputName, null, null);
    }

}
