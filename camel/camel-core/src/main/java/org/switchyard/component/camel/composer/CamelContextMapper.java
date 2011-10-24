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

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(Message source, Context context) throws Exception {
        Exchange exchange = source.getExchange();
        for (Map.Entry<String,Object> entry : exchange.getProperties().entrySet()) {
            String name = entry.getKey();
            if (matches(name)) {
                Object value = entry.getValue();
                if (value != null) {
                    context.setProperty(name, value);
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
        for (Scope scope : Scope.values()) {
            for (Property property : context.getProperties(scope)) {
                String name = property.getName();
                if (matches(name)) {
                    Object value = property.getValue();
                    if (value != null) {
                        exchange.setProperty(name, value);
                    }
                }
            }
        }
    }

}
