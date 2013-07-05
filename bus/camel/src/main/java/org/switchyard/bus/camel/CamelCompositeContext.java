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

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.common.camel.ContextPropertyUtil;
import org.switchyard.label.BehaviorLabel;

/**
 * Implementation of {@link Context} specific to Camel Exchange Bus.
 */
public class CamelCompositeContext implements Context {

    private final Exchange _exchange;
    private final Message _message;

    /**
     * Creates new Context with {@link Scope#EXCHANGE} as default scope.
     * 
     * In addition the current IN message of exchange will be used to map {@link Scope#MESSAGE} scope.
     * 
     * @param exchange Exchange to use.
     */
    public CamelCompositeContext(Exchange exchange) {
        this(exchange, exchange.getIn());
    }

    /**
     * Creates new Context with given as default scope.
     * 
     * {@link Scope#MESSAGE} scope will be mapped to passed message instance.
     * 
     * @param exchange Exchange to use.
     * @param message Message to use.
     */
    public CamelCompositeContext(Exchange exchange, Message message) {
        this._exchange = exchange;
        this._message = message;
    }

    @Override
    public void mergeInto(Context context) {
        for (Property property : getProperties()) {
            if (ContextPropertyUtil.isReservedProperty(property.getName(), property.getScope())
                    || property.hasLabel(BehaviorLabel.TRANSIENT.label())) {
                continue;
            }

            context.setProperty(property.getName(), property.getValue(), property.getScope())
                .addLabels(property.getLabels());
        }
    }

    @Override
    public Property getProperty(String name) {
        Property property = getProperty(name, Scope.MESSAGE);
        return property == null ? getProperty(name, Scope.EXCHANGE) : property;
    }

    @Override
    public Property getProperty(String name, Scope scope) {
        switch (scope) {
        case EXCHANGE:
            return getExchangeProperty(name);
        default:
            return getMessageProperty(name);
        }
    }

    private Property getMessageProperty(String name) {
        if (_message.hasHeaders() && _message.getHeaders().containsKey(name)) {
            return new CamelMessageProperty(_message, name);
        }
        return null;
    }

    private Property getExchangeProperty(String name) {
        if (_exchange.hasProperties() && _exchange.getProperties().containsKey(name)) {
            return new CamelExchangeProperty(_exchange, name);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getPropertyValue(String name) {
        Property property = getProperty(name);
        return property == null ? null : (T) property.getValue();
    }

    @Override
    public Set<Property> getProperties() {
        Set<Property> properties = new HashSet<Property>();
        properties.addAll(getProperties(Scope.EXCHANGE));
        properties.addAll(getProperties(Scope.MESSAGE));
        return properties;
    }

    @Override
    public Set<Property> getProperties(Scope scope) {
        Set<Property> properties = new HashSet<Property>();
        switch (scope) {
        case EXCHANGE:
            if (_exchange.hasProperties()) {
                for (String prop : _exchange.getProperties().keySet()) {
                    properties.add(new CamelExchangeProperty(_exchange, prop));
                }
            }
            break;
        default:
            if (_message.hasHeaders()) {
                for (String prop : _message.getHeaders().keySet()) {
                    properties.add(new CamelMessageProperty(_message, prop));
                }
            }
            break;
        }
        return properties;
    }

    @Override
    public Set<Property> getProperties(String label) {
        Set<Property> properties = new HashSet<Property>();
        for (Property property : getProperties()) {
            if (property.hasLabel(label)) {
                properties.add(property);
            }
        }
        return properties;
    }

    @Override
    public void removeProperty(Property property) {
        switch (property.getScope()) {
        case EXCHANGE:
            _exchange.removeProperty(property.getName());
            break;
        default:
            _message.removeHeader(property.getName());
            break;
        }
    }

    @Override
    public void removeProperties() {
        for (Property property : getProperties()) {
            removeProperty(property);
        }
    }

    @Override
    public void removeProperties(Scope scope) {
        for (Property property : getProperties(scope)) {
            removeProperty(property);
        }
    }

    @Override
    public void removeProperties(String label) {
        for (Property property : getProperties()) {
            if (property.hasLabel(label)) {
                removeProperty(property);
            }
        }
    }

    @Override
    public Property setProperty(String name, Object val) {
        return setProperty(name, val, Scope.MESSAGE);
    }

    @Override
    public Property setProperty(String name, Object val, Scope scope) {
        switch (scope) {
        case EXCHANGE:
            _exchange.setProperty(name, val);
            break;
        default:
            _message.setHeader(name, val);
            break;
        }
        return getProperty(name, scope);
    }

    @Override
    public Context setProperties(Set<Property> properties) {
        for (Property property : properties) {
            Set<String> labels = property.getLabels();
            setProperty(property.getName(), property.getValue(), property.getScope())
                .addLabels(labels.toArray(new String[labels.size()]));
        }
        return this;
    }

}
