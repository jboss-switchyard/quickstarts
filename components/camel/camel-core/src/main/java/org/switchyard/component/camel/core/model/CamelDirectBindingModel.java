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
package org.switchyard.component.camel.core.model;

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Represents the configuration settings for a Direct endpoint in Camel. The 
 * direct component provides direct, synchronous invocation of any consumers 
 * when a producer sends a message exchange. This endpoint can be used to 
 * connect existing routes in the same camel context.
 */
public interface CamelDirectBindingModel extends CamelBindingModel {

    /**
     * The name can be any String to uniquely identify the endpoint.
     * @return the unique identifier for the endpoint
     */
    String getEndpointName();

    /**
     * The name can be any String to uniquely identify the endpoint.
     * @param name the unique identifier for the endpoint
     * @return a reference to this Direct binding model
     */
    CamelDirectBindingModel setEndpointName(String name);

}
