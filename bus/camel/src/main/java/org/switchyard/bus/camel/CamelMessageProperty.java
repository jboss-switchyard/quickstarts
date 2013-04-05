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
