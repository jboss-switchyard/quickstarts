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

import org.hornetq.api.core.HornetQPropertyConversionException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientMessage;
import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.common.composer.BaseRegexContextMapper;
import org.switchyard.component.common.label.ComponentLabel;
import org.switchyard.component.common.label.EndpointLabel;

/**
 * HornetQContextMapper.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class HornetQContextMapper extends BaseRegexContextMapper<HornetQBindingData> {

    private static final String[] HORNETQ_LABELS = new String[]{ComponentLabel.HORNETQ.label(), EndpointLabel.JMS.label()};

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(HornetQBindingData source, Context context) throws Exception {
        ClientMessage clientMessage = source.getClientMessage();
        for (SimpleString key : clientMessage.getPropertyNames()) {
            String name = key.toString();
            if (matches(name)) {
                Object value = clientMessage.getObjectProperty(key);
                if (value != null) {
                    context.setProperty(name, value, Scope.IN).addLabels(HORNETQ_LABELS);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapTo(Context context, HornetQBindingData target) throws Exception {
        ClientMessage clientMessage = target.getClientMessage();
        for (Property property : context.getProperties(Scope.OUT)) {
            String name = property.getName();
            if (matches(name)) {
                Object value = property.getValue();
                if (value != null) {
                    try {
                        clientMessage.putObjectProperty(name, value);
                    } catch (HornetQPropertyConversionException pce) {
                        // ignore and keep going (here just to keep checkstyle happy)
                        pce.getMessage();
                    }
                }
            }
        }
    }

}
