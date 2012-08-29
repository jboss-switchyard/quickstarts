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
package org.switchyard.component.jca.composer;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.common.composer.BaseRegexContextMapper;

/**
 * JCAJMSContextMapper.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class JMSContextMapper extends BaseRegexContextMapper<JMSBindingData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(JMSBindingData source, Context context) throws Exception {
        Message message = source.getMessage();
        Enumeration<?> e = message.getPropertyNames();
        while (e.hasMoreElements()) {
            String key = e.nextElement().toString();
            if (matches(key)) {
                Object value = null;
                try {
                    value = message.getObjectProperty(key);
                } catch (JMSException pce) {
                    // ignore and keep going (here just to keep checkstyle happy)
                    pce.getMessage();
                }
                if (value != null) {
                    // JMS Message properties -> Context EXCHANGE properties
                    context.setProperty(key, value, Scope.EXCHANGE)
                            .addLabels(JCAComposition.JCA_MESSAGE_PROPERTY);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapTo(Context context, JMSBindingData target) throws Exception {
        Message message = target.getMessage();
        for (Property property : context.getProperties(Scope.EXCHANGE)) {
            String name = property.getName();
            if (matches(name)) {
                Object value = property.getValue();
                if (value != null) {
                    try {
                        // Context EXCHANGE properties -> JMS Message properties
                        message.setObjectProperty(name, value);
                    } catch (JMSException pce) {
                        // ignore and keep going (here just to keep checkstyle happy)
                        pce.getMessage();
                    }
                }
            }
        }
    }

}
