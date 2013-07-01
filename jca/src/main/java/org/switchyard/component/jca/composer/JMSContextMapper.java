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
package org.switchyard.component.jca.composer;

import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.component.common.composer.BaseRegexContextMapper;
import org.switchyard.component.common.label.ComponentLabel;
import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.component.common.label.PropertyLabel;

/**
 * JMSContextMapper.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class JMSContextMapper extends BaseRegexContextMapper<JMSBindingData> {

    /** JMSDestination header. */
    public static final String HEADER_JMS_DESTINATION = "javax.jms.JMSDestination";
    /** JMSDeliveryMode header. */
    public static final String HEADER_JMS_DELIVERY_MODE = "javax.jms.JMSDeliveryMode";
    /** JMSMessageID header. */
    public static final String HEADER_JMS_MESSAGE_ID = "javax.jms.JMSMessageID";
    /** JMSTimestamp header. */
    public static final String HEADER_JMS_TIMESTAMP = "javax.jms.JMSTimestamp";
    /** JMSCorrelationID header. */
    public static final String HEADER_JMS_CORRELATION_ID = "javax.jms.JMSCorrelationID";
    /** JMSReplyTo header. */
    public static final String HEADER_JMS_REPLY_TO = "javax.jms.JMSReplyTo";
    /** JMSRedelivered header. */
    public static final String HEADER_JMS_REDELIVERED = "javax.jms.JMSRedelivered";
    /** JMSType header. */
    public static final String HEADER_JMS_TYPE = "javax.jms.JMSType";
    /** JMSExpiration header. */
    public static final String HEADER_JMS_EXPIRATION = "javax.jms.JMSExpiration";
    /** JMSPriority header. */
    public static final String HEADER_JMS_PRIORITY = "javax.jms.JMSPriority";
    
    private static final String[] JMS_HEADER_LABELS = new String[]{ComponentLabel.JCA.label(), EndpointLabel.JMS.label(), PropertyLabel.HEADER.label()};
    private static final String[] JMS_PROPERTY_LABELS = new String[]{ComponentLabel.JCA.label(), EndpointLabel.JMS.label(), PropertyLabel.PROPERTY.label()};
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(JMSBindingData source, Context context) throws Exception {
        Message message = source.getMessage();
        
        // process JMS headers
        if (matches(HEADER_JMS_DESTINATION)) {
            context.setProperty(HEADER_JMS_DESTINATION, message.getJMSDestination()).addLabels(JMS_HEADER_LABELS);
        }
        if (matches(HEADER_JMS_DELIVERY_MODE)) {
            context.setProperty(HEADER_JMS_DELIVERY_MODE, message.getJMSDeliveryMode()).addLabels(JMS_HEADER_LABELS);
        }
        if (matches(HEADER_JMS_EXPIRATION)) {
            context.setProperty(HEADER_JMS_EXPIRATION, message.getJMSExpiration()).addLabels(JMS_HEADER_LABELS);
        }
        if (matches(HEADER_JMS_PRIORITY)) {
            context.setProperty(HEADER_JMS_PRIORITY, message.getJMSPriority()).addLabels(JMS_HEADER_LABELS);
        }
        if (matches(HEADER_JMS_MESSAGE_ID)) {
            context.setProperty(HEADER_JMS_MESSAGE_ID, message.getJMSMessageID()).addLabels(JMS_HEADER_LABELS);
        }
        if (matches(HEADER_JMS_TIMESTAMP)) {
            context.setProperty(HEADER_JMS_TIMESTAMP, message.getJMSTimestamp()).addLabels(JMS_HEADER_LABELS);
        }
        if (matches(HEADER_JMS_CORRELATION_ID)) {
            context.setProperty(HEADER_JMS_CORRELATION_ID, message.getJMSCorrelationID()).addLabels(JMS_HEADER_LABELS);
        }
        if (matches(HEADER_JMS_REPLY_TO)) {
            context.setProperty(HEADER_JMS_REPLY_TO, message.getJMSReplyTo()).addLabels(JMS_HEADER_LABELS);
        }
        if (matches(HEADER_JMS_TYPE)) {
            context.setProperty(HEADER_JMS_TYPE, message.getJMSType()).addLabels(JMS_HEADER_LABELS);
        }
        if (matches(HEADER_JMS_REDELIVERED)) {
            context.setProperty(HEADER_JMS_REDELIVERED, message.getJMSRedelivered()).addLabels(JMS_HEADER_LABELS);
        }
        
        // process JMS properties
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
                    context.setProperty(key, value).addLabels(JMS_PROPERTY_LABELS);
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
        for (Property property : context.getProperties()) {
            String name = property.getName();

            if (matches(name)) {
                Object value = property.getValue();
                if (value == null) {
                    continue;
                }

                try {
                    // process JMS headers
                    if (name.equals(HEADER_JMS_DESTINATION)) {
                        message.setJMSDestination(Destination.class.cast(value));
                    } else if (name.equals(HEADER_JMS_DELIVERY_MODE)) {
                        message.setJMSDeliveryMode(Integer.parseInt(value.toString()));
                    } else if (name.equals(HEADER_JMS_EXPIRATION)) {
                        message.setJMSExpiration(Long.parseLong(value.toString()));
                    } else if (name.equals(HEADER_JMS_PRIORITY)) {
                        message.setJMSPriority(Integer.parseInt(value.toString()));
                    } else if (name.equals(HEADER_JMS_MESSAGE_ID)) {
                        message.setJMSMessageID(value.toString());
                    } else if (name.equals(HEADER_JMS_TIMESTAMP)) {
                        message.setJMSTimestamp(Long.parseLong(value.toString()));
                    } else if (name.equals(HEADER_JMS_CORRELATION_ID)) {
                        message.setJMSCorrelationID(value.toString());
                    } else if (name.equals(HEADER_JMS_REPLY_TO)) {
                        message.setJMSReplyTo(Destination.class.cast(value));
                    } else if (name.equals(HEADER_JMS_TYPE)) {
                        message.setJMSType(value.toString());
                    } else if (name.equals(HEADER_JMS_REDELIVERED)) {
                        message.setJMSRedelivered(Boolean.parseBoolean(value.toString()));

                    // process JMS properties
                    } else {
                        message.setObjectProperty(name, value);
                    }
                } catch (Throwable t) {
                    continue;
                }
            }
        }
        
    }

}
