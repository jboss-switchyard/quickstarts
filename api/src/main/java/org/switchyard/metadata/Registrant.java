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
 * Represents a component which registers a service and/or reference within a
 * service domain.
 */
public interface Registrant {

    /**
     * Indicates whether the registrant is a binding or implementation.
     * @return true if binding, false otherwise
     */
    boolean isBinding();
    
    /**
     * Indicates whether the registrant is a binding or implementation.
     * @return true if implementation, false otherwise
     */
    boolean isImplementation();
    
    /**
     * Returns a config representation based on the type of registrant.  
     * @param <T> Implementations will return ComponentImplementationModel, while 
     * bindings will return BindingModel.
     * @return type-specific configuration model
     */
    <T> T getConfig();
}
