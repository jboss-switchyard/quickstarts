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
 * binding.jca/inboundInteraction model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public interface InboundInteractionModel extends Model {

    /**
     * get Listener model.
     * 
     * @return {@link ListenerModel}
     */
    ListenerModel getListener();
    
    /**
     * set Listener model.
     * 
     * @param listener {@link ListenerModel} to set
     * @return {@link InboundInteractionModel} to support method chaining
     */
    InboundInteractionModel setListener(final ListenerModel listener);
    
    /**
     * get Endpoint model.
     * 
     * @return {@link EndpointModel}
     */
    EndpointModel getEndpoint();
    
    /**
     * set Endpoint model.
     * 
     * @param endpoint {@link EndpointModel} to set
     * @return {@link InboundInteractionModel} to support method chaining
     */
    InboundInteractionModel setEndpoint(final EndpointModel endpoint);

    /**
     * get transacted.
     * 
     * @return true if transacted
     */
    boolean isTransacted();
    
    /**
     * set transacted.
     * 
     * @param transacted true if transacted
     * @return {@link InboundInteractionModel} to suport method chaining
     */
    InboundInteractionModel setTransacted(boolean transacted);
    
    /**
     * get BatchCommitModel.
     * @return {@link BatchCommitModel}
     */
    BatchCommitModel getBatchCommit();
    
    /**
     * set BatchCommitModel.
     * @param batchCommit {@link BatchCommitModel} to set
     * @return {@link InboundInteractionModel} to support method chaining
     */
    InboundInteractionModel setBatchCommit(final BatchCommitModel batchCommit);
}
