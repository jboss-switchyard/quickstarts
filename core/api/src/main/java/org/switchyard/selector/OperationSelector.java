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

package org.switchyard.selector;

import javax.xml.namespace.QName;

/**
 * OperationSelector which determine the operation to be mapped to the binding.
 * 
 * @param <T> the type of source object
 */
public interface OperationSelector<T> {

    /**
     * Select a operation.
     * 
     * @param content message content to search for operation
     * @return operation QName
     * @throws Exception if failed to determine the operation
     */
    public QName selectOperation(T content) throws Exception;
    
    /**
     * Sets default namespace.
     * @param namespace namespace
     * @return this instance for chaining
     */
    public OperationSelector<T> setDefaultNamespace(String namespace);
    
    /**
     * Gets default namespace.
     * @return namespace
     */
    public String getDefaultNamespace();

}
