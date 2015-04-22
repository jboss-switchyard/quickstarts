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
package org.switchyard.deploy;

import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.metadata.Registrant;

/**
 * Represents implementation metadata for a service or reference.
 */
public class Implementation implements Registrant {
    
    private ComponentImplementationModel _config;
    
    /**
     * Create a new Implementation instance.
     * @param config implementation configuration
     */
    public Implementation(ComponentImplementationModel config) {
        _config = config;
    }

    @Override
    public boolean isBinding() {
        return false;
    }

    @Override
    public boolean isImplementation() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ComponentImplementationModel getConfig() {
        return _config;
    }

}
