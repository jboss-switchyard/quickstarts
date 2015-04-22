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
package org.switchyard.component.jca.config.model;

import org.switchyard.config.model.Model;

/**
 * binding.jca/inboundInteraction/batchCommit model.
 */
public interface BatchCommitModel extends Model {
    
    /**
     * get batch timeout.
     * @return batch timeout in milliseconds
     */
    long getBatchTimeout();
    
    /**
     * set batch timeout.
     * @param timeout batch timeout in milliseconds
     * @return {@link BatchCommitModel} to support method chaining
     */
    BatchCommitModel setBatchTimeout(long timeout);
    
    /**
     * get batch size.
     * @return batch size
     */
    int getBatchSize();
    
    /**
     * set batch size.
     * @param size batch size.
     * @return {@link BatchCommitModel} to support method chaining
     */
    BatchCommitModel setBatchSize(int size);
    
}
