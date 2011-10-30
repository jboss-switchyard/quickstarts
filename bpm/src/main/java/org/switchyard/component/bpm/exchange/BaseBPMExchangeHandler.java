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
import static org.switchyard.component.bpm.common.ProcessConstants.ACTION_TYPE_VAR;
import static org.switchyard.component.bpm.common.ProcessConstants.PROCESS_EVENT_TYPE_VAR;
import static org.switchyard.component.bpm.common.ProcessConstants.PROCESS_EVENT_VAR;
import static org.switchyard.component.bpm.common.ProcessConstants.PROCESS_INSTANCE_ID_VAR;

import org.apache.log4j.Logger;
import org.switchyard.BaseHandler;
import org.switchyard.Context;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.component.bpm.common.ProcessActionType;
import org.switchyard.component.bpm.config.model.ProcessActionModel;

/**
 * Contains base BPMExchangeHandler functionality and/or utility methods.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseBPMExchangeHandler extends BaseHandler implements BPMExchangeHandler {

    private static final Logger LOGGER = Logger.getLogger(BaseBPMExchangeHandler.class);

    /**
     * Gets the ProcessActionType from the Exchange Context.
     * @param context the Exchange Context
     * @param model the associated ProcessActionModel
     * @return the ProcessActionType
     */
    protected ProcessActionType getProcessActionType(Context context, ProcessActionModel model) {
        if (model != null) {
            ProcessActionType pat = model.getType();
            if (pat != null) {
                return pat;
            }
        }
        Property property = context.getProperty(ACTION_TYPE_VAR, IN);
        if (property != null) {
            Object value = property.getValue();
            if (value instanceof ProcessActionType) {
                return (ProcessActionType)value;
            } else if (value instanceof String) {
                return ProcessActionType.fromAction((String)value);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            String msg = new StringBuilder()
                .append(getNullParameterMessage(null, ACTION_TYPE_VAR))
                .append("; defaulting to: ")
                .append(ProcessActionType.START_PROCESS.action())
                .toString();
            LOGGER.debug(msg);
        }
        return ProcessActionType.START_PROCESS;
    }

    /**
     * Gets the process instance id from the Exchange Context.
     * @param context the Exchange Context
     * @return the process instance id
     */
    protected Long getProcessInstanceId(Context context) {
        Property property = context.getProperty(PROCESS_INSTANCE_ID_VAR, IN);
        if (property != null) {
            Object value = property.getValue();
            if (value instanceof Long) {
                return (Long)value;
            } else if (value instanceof Number) {
                return Long.valueOf(((Number)value).longValue());
            } else if (value instanceof String) {
                return Long.valueOf((String)value);
            }
        }
        return null;
    }

    /**
     * Gets the process event type from the Exchange Context.
     * @param context the Exchange Context
     * @param model the associated ProcessActionModel
     * @return the process event type
     */
    protected String getProcessEventType(Context context, ProcessActionModel model) {
        if (model != null) {
            String pet = model.getEventType();
            if (pet != null) {
                return pet;
            }
        }
        Property property = context.getProperty(PROCESS_EVENT_TYPE_VAR, IN);
        if (property != null) {
            Object value = property.getValue();
            if (value instanceof String) {
                return (String)value;
            } else if (value != null) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    /**
     * Gets the process event from the Exchange Context.
     * @param context the Exchange Context
     * @param message the Message
     * @return the process event
     */
    protected Object getProcessEvent(Context context, Message message) {
        Property property = context.getProperty(PROCESS_EVENT_VAR, IN);
        if (property != null) {
            Object value = property.getValue();
            if (value != null) {
                return value;
            } else if (message != null) {
                return message.getContent();
            }
        }
        return null;
    }

    /**
     * Creates and error message for a null parameter.
     * @param processActionType the optional process action type
     * @param parameterName the name of the parameter
     * @return the error message
     */
    protected String getNullParameterMessage(ProcessActionType processActionType, String parameterName) {
        StringBuilder sb = new StringBuilder("implementation.bpm: ");
        if (processActionType != null) {
            sb.append("[");
            sb.append(processActionType.action());
            sb.append("] ");
        }
        sb.append(parameterName);
        sb.append(" == null");
        return sb.toString();
    }

    /**
     * Throws a new exception for a null parameter.
     * @param processActionType the optional process action type
     * @param parameterName the name of the parameter
     * @throws HandlerException the exception thrown for the null parameter
     */
    protected void throwNullParameterException(ProcessActionType processActionType, String parameterName) throws HandlerException {
        throw new HandlerException(getNullParameterMessage(processActionType, parameterName));
    }

}
