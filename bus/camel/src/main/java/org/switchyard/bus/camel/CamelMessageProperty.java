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
package org.switchyard.bus.camel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Message;
import org.switchyard.Property;
import org.switchyard.Scope;

/**
 * Property implementation to handle {@link Scope#MESSAGE}.
 */
public class CamelMessageProperty extends CamelPropertyBase implements Property {

    private final Message _message;
    private final String _name;

    /**
     * Creates new message property.
     * 
     * @param message Message instance.
     * @param name Header name.
     */
    public CamelMessageProperty(Message message, String name) {
        this._message = message;
        this._name = name;
    }

    @Override
    public Scope getScope() {
        return Scope.MESSAGE;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Object getValue() {
        return _message.getHeader(_name);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Set<String>> getLabelsBag() {
        if (!_message.getHeaders().containsKey(CamelExchange.LABELS)) {
            _message.setHeader(CamelExchange.LABELS, new HashMap<String, Set<String>>());
        }
        return _message.getHeader(CamelExchange.LABELS, Map.class);
    }
}
