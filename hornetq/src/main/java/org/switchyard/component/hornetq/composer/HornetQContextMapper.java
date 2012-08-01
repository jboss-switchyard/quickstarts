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
package org.switchyard.component.hornetq.composer;

import static org.switchyard.Scope.EXCHANGE;
import static org.switchyard.component.hornetq.composer.HornetQComposition.HORNETQ_MESSAGE_PROPERTY;

import org.hornetq.api.core.PropertyConversionException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientMessage;
import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.component.common.composer.BaseRegexContextMapper;

/**
 * HornetQContextMapper.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class HornetQContextMapper extends BaseRegexContextMapper<ClientMessage> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(ClientMessage source, Context context) throws Exception {
        for (SimpleString key : source.getPropertyNames()) {
            String name = key.toString();
            if (matches(name)) {
                Object value = source.getObjectProperty(key);
                if (value != null) {
                    // HornetQ ClientMessage properties -> Context EXCHANGE properties
                    context.setProperty(name, value, EXCHANGE).addLabels(HORNETQ_MESSAGE_PROPERTY);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapTo(Context context, ClientMessage target) throws Exception {
        for (Property property : context.getProperties(EXCHANGE)) {
            String name = property.getName();
            if (matches(name)) {
                Object value = property.getValue();
                if (value != null) {
                    try {
                        // Context EXCHANGE properties -> HornetQ ClientMessage properties
                        target.putObjectProperty(name, value);
                    } catch (PropertyConversionException pce) {
                        // ignore and keep going (here just to keep checkstyle happy)
                        pce.getMessage();
                    }
                }
            }
        }
    }

}
