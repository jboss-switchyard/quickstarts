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
 * Representation of an operation on a ServiceInterface.
 * <p/>
 * Each operation has:
 * <ul>
 * <li>an exchange pattern (IN_OUT, IN_ONLY)
 * <li>a name
 * <li>an input message referenced type
 * <li>an (optional) output message type
 * <li>an (optional) fault message type
 * </ul>
 * <br>
 * The mapping of operation and message names is defined by the concrete 
 * implementation of ServiceInterface.  For example, the expected mapping of 
 * a Java interface would be Java method name to ServiceInterface operation name.
 */
public interface ServiceOperation {

    /**
     * The exchange pattern for the operation.
     * @return exchange pattern
     */
    ExchangePattern getExchangePattern();
    /**
     * The name of the operation.
     * @return operation name
     */
    String getName();
    
    /**
     * The name of the input message type.
     * @return input message type or null if no type information is available.
     * @see org.switchyard.annotations.OperationTypes
     */
    QName getInputType();
    
    /**
     * The name of the output message type.
     * @return output message name or null if no type information is available.
     * @see org.switchyard.annotations.OperationTypes
     */
    QName getOutputType();
    
    /**
     * The name of the output message type.
     * @return output message name or null if no type information is available.
     * @see org.switchyard.annotations.OperationTypes
     */
    QName getFaultType();
}
