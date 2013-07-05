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

package org.switchyard.admin.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.switchyard.admin.Component;

/**
 * BaseComponent
 * 
 * Basic implementation for Component.
 */
public class BaseComponent implements Component {

    private Map<String, String> _properties;
    private String _name;
    private Set<String> _types;

    /**
     * Create a new BaseComponent.
     * 
     * @param name the name of the component.
     * @param types the type of the component.
     * @param properties the configuration properties of this component.
     */
    public BaseComponent(String name, Collection<String> types, Map<String, String> properties) {
        _name = name;
        _types = new HashSet<String>();
        if (types != null) {
            _types.addAll(types);
        }
        _properties = new HashMap<String, String>();
        if (properties != null) {
            _properties.putAll(properties);
        }
    }

    @Override
    public Map<String, String> getProperties() {
        return _properties;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Set<String> getTypes() {
        return _types;
    }
}
