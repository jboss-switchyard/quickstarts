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
package org.switchyard.component.bpm.exchange;

import static org.switchyard.Scope.IN;
import static org.switchyard.component.bpm.process.ProcessConstants.PROCESS_ACTION_TYPE_VAR;
import static org.switchyard.component.bpm.process.ProcessConstants.PROCESS_EVENT_VAR;
import static org.switchyard.component.bpm.process.ProcessConstants.PROCESS_EVENT_TYPE_VAR;
import static org.switchyard.component.bpm.process.ProcessConstants.PROCESS_INSTANCE_ID_VAR;
import static org.switchyard.component.bpm.process.ProcessConstants.PROCESS_NAMESPACE;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.switchyard.BaseHandler;
import org.switchyard.Context;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.common.type.Classes;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.bpm.process.ProcessActionType;
import org.switchyard.exception.SwitchYardException;

/**
 * Contains base BpmExchangeHandler functionality and/or utility methods.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseBpmExchangeHandler extends BaseHandler implements BpmExchangeHandler {

    /**
     * Gets the ProcessActionType from the Exchange Context.
     * @param context the Exchange Context
     * @return the ProcessActionType
     */
    protected ProcessActionType getProcessActionType(Context context) {
        Property property = context.getProperty(PROCESS_ACTION_TYPE_VAR, IN);
        Object value = property != null ? property.getValue() : null;
        if (value instanceof ProcessActionType) {
            return (ProcessActionType)value;
        } else if (value instanceof QName) {
            return ProcessActionType.valueOf((QName)value);
        } else if (value instanceof String) {
            return ProcessActionType.valueOf(XMLHelper.createQName(PROCESS_NAMESPACE, (String)value));
        }
        return null;
    }

    /**
     * Gets the process instance id from the Exchange Context.
     * @param context the Exchange Context
     * @return the process instance id
     */
    protected Long getProcessInstanceId(Context context) {
        Property property = context.getProperty(PROCESS_INSTANCE_ID_VAR, IN);
        Object value = property != null ? property.getValue() : null;
        if (value instanceof Long) {
            return (Long)value;
        } else if (value instanceof Number) {
            return Long.valueOf(((Number)value).longValue());
        } else if (value instanceof String) {
            return Long.valueOf((String)value);
        }
        return null;
    }

    /**
     * Gets the process event type from the Exchange Context.
     * @param context the Exchange Context
     * @return the process event type
     */
    protected String getProcessEventType(Context context) {
        Property property = context.getProperty(PROCESS_EVENT_TYPE_VAR, IN);
        Object value = property != null ? property.getValue() : null;
        if (value instanceof String) {
            return (String)value;
        } else if (value != null) {
            return String.valueOf(value);
        }
        return null;
    }

    /**
     * Gets the process event from the Exchange Context.
     * @param context the Exchange Context
     * @return the process event
     */
    protected Object getProcessEvent(Context context, Message message) {
        Property property = context.getProperty(PROCESS_EVENT_VAR, IN);
        Object value = property != null ? property.getValue() : null;
        if (value == null && message != null) {
            value = message.getContent();
        }
        return value;
    }

    /**
     * Creates a URL for the given resource location.
     * @param location the resource location (http://, https://, file:, or classpath location)
     * @return the resource URL
     */
    protected URL getResourceURL(String location) {
        URL url;
        try {
            if (location.startsWith("http:") || location.startsWith("https:")) {
                url = new URL(location);
            } else if (location.startsWith("file:")) {
                url = new File(location.substring(5)).toURI().toURL();
            } else {
                url = Classes.getResource(location, getClass());
            }
        } catch (IOException ioe) {
            throw new SwitchYardException(ioe);
        }
        return url;
    }

}
