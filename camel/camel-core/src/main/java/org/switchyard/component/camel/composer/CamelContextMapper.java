/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel.composer;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.composer.BaseContextMapper;

/**
 * CamelContextMapper.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class CamelContextMapper extends BaseContextMapper<Message> {

    private static final Scope[] IN_OUT = new Scope[]{Scope.IN, Scope.OUT};

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(Message source, Context context) throws Exception {
        Scope scope;
        Exchange exchange = source.getExchange();
        if (exchange.getIn() == source) {
            scope = Scope.IN;
        } else {
            scope = Scope.OUT;
        }
        for (Map.Entry<String,Object> header : source.getHeaders().entrySet()) {
            String name = header.getKey();
            if (matches(name)) {
                Object value = header.getValue();
                if (value != null) {
                    // Camel Message headers -> Context ${scope} properties
                    context.setProperty(name, value, scope);
                }
            }
        }
        if (exchange != null) {
            for (Map.Entry<String,Object> property : exchange.getProperties().entrySet()) {
                String name = property.getKey();
                if (matches(name)) {
                    Object value = property.getValue();
                    if (value != null) {
                        // Camel Exchange properties -> Context EXCHANGE properties
                        context.setProperty(name, value, Scope.EXCHANGE);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapTo(Context context, Message target) throws Exception {
        Exchange exchange = target.getExchange();
        for (Scope scope : IN_OUT) {
            for (Property property : context.getProperties(scope)) {
                String name = property.getName();
                if (matches(name)) {
                    Object value = property.getValue();
                    if (value != null) {
                        // Context ${scope} properties -> Camel Message headers
                        target.setHeader(name, value);
                    }
                }
            }
        }
        if (exchange != null) {
            for (Property property : context.getProperties(Scope.EXCHANGE)) {
                String name = property.getName();
                if (matches(name)) {
                    Object value = property.getValue();
                    if (value != null) {
                        // Context EXCHANGE properties -> Camel Exchange properties
                        exchange.setProperty(name, value);
                    }
                }
            }
        }
    }

}
