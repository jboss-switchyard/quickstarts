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

import org.switchyard.config.model.composite.BindingModel;

/**
 * A binding definition for JCA gateway.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public interface JCABindingModel extends BindingModel {
    
    /**
     * get OutboundConnection model.
     * 
     * @return {@link OutboundConnectionModel}
     */
    OutboundConnectionModel getOutboundConnection();
    
    /**
     * set OutboundConnection model.
     * 
     * @param config {@link OutboundConnectionModel} to set
     * @return {@JCABindingModel} to support method chaining
     */
    JCABindingModel setOutboundConnection(final OutboundConnectionModel config);
    
    /**
     * get InboundConnection model.
     * 
     * @return {@link InboundConnectionModel}
     */
    InboundConnectionModel getInboundConnection();
    
    /**
     * set InboundConnection model.
     * 
     * @param config {@link InboundConnectionModel} to set
     * @return {@link JCABindingModel} to support method chaining
     */
    JCABindingModel setInboundConnection(final InboundConnectionModel config);
    
    /**
     * get OutboundInteraction model.
     * 
     * @return {@link OutboundInteractionModel}
     */
    OutboundInteractionModel getOutboundInteraction();
    
    /**
     * set OutboundInteraction model.
     * 
     * @param config {@link OutboundInteractionModel} to set
     * @return {@link JCABindingModel} to support method chaining
     */
    JCABindingModel setOutboundInteraction(final OutboundInteractionModel config);
    
    /**
     * get InboundInteraction model.
     * 
     * @return {@link InboundInteractionModel}
     */
    InboundInteractionModel getInboundInteraction();
    
    /**
     * set InboundInteraction model.
     * 
     * @param config {@link InboundInteractionModel} to set
     * @return {@link JCABinding} to support method chaining
     */
    JCABindingModel setInboundInteraction(final InboundInteractionModel config);
    
}
