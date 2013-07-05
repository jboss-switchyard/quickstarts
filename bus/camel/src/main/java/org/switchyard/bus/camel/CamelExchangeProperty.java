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

import org.apache.camel.Exchange;
import org.switchyard.Scope;

/**
 * Property implementation to handle {@link Scope#EXCHANGE}.
 */
public class CamelExchangeProperty extends CamelPropertyBase {

    private final Exchange _exchange;
    private final String _name;

    /**
     * Creates new exchange property.
     * 
     * @param exchange Exchange instance.
     * @param name Property name.
     */
    public CamelExchangeProperty(Exchange exchange, String name) {
        this._exchange = exchange;
        this._name = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Set<String>> getLabelsBag() {
        if (!_exchange.getProperties().containsKey(CamelExchange.LABELS)) {
            _exchange.setProperty(CamelExchange.LABELS, new HashMap<String, Set<String>>());
        }
        return _exchange.getProperty(CamelExchange.LABELS, Map.class);
    }

    @Override
    public Scope getScope() {
        return Scope.EXCHANGE;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Object getValue() {
        return _exchange.getProperty(_name);
    }

}
