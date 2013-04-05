/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
