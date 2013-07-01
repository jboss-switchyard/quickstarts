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

/**
 * binding.jca/inboundInteraction/endpoint model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public interface EndpointModel extends BasePropertyContainerModel {

    /**
     * get Endpoint class name.
     * 
     * @return Endpoint class name
     */
    String getEndpointClassName();
    
    /**
     * set Endpoint class name.
     * 
     * @param endpoint Endpoint class name to set
     * @return {@link EndpointModel} to suport method chaining
     */
    EndpointModel setEndpointClassName(final String endpoint);

}
