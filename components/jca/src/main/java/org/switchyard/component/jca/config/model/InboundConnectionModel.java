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
 * binding.jca/inboundConnection model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public interface InboundConnectionModel extends Model {

    /**
     * get ResouceAdapter model.
     * 
     * @return {@link ResourceAdamterModel}
     */
    ResourceAdapterModel getResourceAdapter();
    
    /**
     * set ResourceAdapter model.
     * 
     * @param config {@link ResourceAdapterModel} to set
     * @return [@link InboundConnectionModel} to support method chaining
     */
    InboundConnectionModel setResourceAdapter(final ResourceAdapterModel config);
    
    /**
     * get ActivationSpec model.
     * 
     * @return {@link ActivationSpecModel}
     */
    ActivationSpecModel getActivationSpec();
    
    /**
     * set ActivationSpec model.
     * 
     * @param config {@link ActivationSpecModel} to set
     * @return {@link InboundConnectionModel} to support method chaining
     */
    InboundConnectionModel setActivationSpec(final ActivationSpecModel config);
    
}
