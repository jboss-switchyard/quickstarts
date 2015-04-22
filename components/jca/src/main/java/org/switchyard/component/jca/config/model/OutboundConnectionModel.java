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
 * OutboundConnection model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public interface OutboundConnectionModel extends Model {

    /**
     * get ResourceAdapter model.
     * 
     * @return {@link ResourceAdapterModel}
     */
    ResourceAdapterModel getResourceAdapter();
    
    /**
     * set ResourceAdapter model.
     * 
     * @param model {@link ResourceAdapterModel} to set
     * @return {@link OutboundConnectionModel} to support method chaining
     */
    OutboundConnectionModel setResourceAdapter(final ResourceAdapterModel model);
    
    /**
     * get Connection model.
     * 
     * @return {@link ConnectionModel}
     */
    ConnectionModel getConnection();
    
    /**
     * set Connection model.
     * 
     * @param model {@link ConnectionModel} to set
     * @return {@link OutboundConnectionModel} to support method chaining
     */
    OutboundConnectionModel setConnection(final ConnectionModel model);
    
    /**
     * get whether the interaction with EIS is managed or not.
     * 
     * @return true if managed
     */
    boolean isManaged();
    
    /**
     * set whether the interaction with EIS is managed or not.
     * 
     * @param managed true if managed
     * @return {@link OutboundConnectionModel} to support method chaining
     */
    OutboundConnectionModel setManaged(final boolean managed);
}
