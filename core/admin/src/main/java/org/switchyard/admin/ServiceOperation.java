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
package org.switchyard.admin;

import javax.xml.namespace.QName;

/**
 * Represents an application service operation.
 */
public interface ServiceOperation extends MessageMetricsAware {

    /**
     * The exchange pattern for the operation.
     * @return exchange pattern
     */
    String getExchangePattern();
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
