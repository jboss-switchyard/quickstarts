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
package org.switchyard.admin;

import org.switchyard.deploy.Lifecycle;

/**
 * Binding
 * 
 * Represents a gateway binding defined on a {@link Service}.
 * 
 * @author Rob Cernich
 */
public interface Binding extends MessageMetricsAware, Lifecycle {
    
    /**
     * @return the type of binding (e.g. soap)
     */
    public String getType();
    
    /**
     * Returns the name of the binding.
     * @return binding name
     */
    public String getName();
    
    /**
     * @return the raw configuration details
     */
    public String getConfiguration();

}
