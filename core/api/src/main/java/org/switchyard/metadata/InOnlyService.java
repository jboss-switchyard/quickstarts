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

/**
 * Implementation of ServiceInterface with a single ExchangePattern.IN_ONLY
 * operation.
 */
public class InOnlyService extends BaseService {
    
    /**
     * Creates a new InOnlyService with a ServiceInterface.DEFAULT_OPERATION
     * operation.
     */
    public InOnlyService() {
        this(DEFAULT_OPERATION);
    }
    
    /**
     * Creates a new InOnlyService with the specified operation name.
     * @param operationName name of the operation
     */
    public InOnlyService(String operationName) {
        super(new InOnlyOperation(operationName));
    }
    
    /**
     * Creates a new InOnlyService with the specified operation metadata.
     * @param operation operation metadata
     */
    public InOnlyService(InOnlyOperation operation) {
        super(operation);
    } 
}
