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

import java.util.LinkedList;
import java.util.List;

import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.metadata.Registrant;

/**
 * Represents binding metadata for a service or reference.
 */
public class Binding implements Registrant {
    
    private List<BindingModel> _configs = new LinkedList<BindingModel>();
    
    /**
     * Create a new Binding instance.
     * @param configs list of binding configs
     */
    public Binding(List<BindingModel> configs) {
        if (configs != null) {
            _configs.addAll(configs);
        }
    }
    
    /**
     * Create a new Binding instance.
     * @param config binding configuration
     */
    public Binding(BindingModel config) {
        _configs.add(config);
    }

    @Override
    public boolean isBinding() {
        return true;
    }

    @Override
    public boolean isImplementation() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BindingModel> getConfig() {
        return _configs;
    }

}
