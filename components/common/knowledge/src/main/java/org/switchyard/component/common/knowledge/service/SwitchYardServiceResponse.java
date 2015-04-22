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
package org.switchyard.component.common.knowledge.service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.switchyard.HandlerException;
import org.switchyard.component.common.knowledge.CommonKnowledgeLogger;
import org.switchyard.component.common.knowledge.CommonKnowledgeMessages;

/**
 * SwitchYardServiceResponse.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SwitchYardServiceResponse {

    private final Object _content;
    private final Map<String, Object> _context = new HashMap<String, Object>();
    private final Object _fault;

    /**
     * Constructs a SwitchYardServiceResponse with the specified context, context, and fault.
     * @param content the content
     * @param context the context
     * @param fault the fault
     */
    SwitchYardServiceResponse(Object content, Map<String, Object> context, Object fault) {
        _content = content;
        if (context != null) {
            _context.putAll(context);
        }
        _fault = fault;
    }

    /**
     * Gets the content.
     * @return the content
     */
    public Object getContent() {
        return _content;
    }

    /**
     * Gets the context.
     * @return the context
     */
    public Map<String, Object> getContext() {
        return _context;
    }

    /**
     * If a fault exists.
     * @return if a fault exists
     */
    public boolean hasFault() {
        return getFault(false) != null;
    }

    /**
     * Gets the unwrapped fault, if it exists.
     * @return the unwrapped fault, or null if it doesn't exist
     */
    public Object getFault() {
        return getFault(true);
    }

    /**
     * Gets the fault, optionally unwrapping it, or null if it doesn't exist.
     * @param unwrap whether to unwrap the fault
     * @return the fault, or null if it doesn't exist
     */
    public Object getFault(boolean unwrap) {
        return unwrap ? unwrapFault(_fault) : _fault;
    }

    private Object unwrapFault(Object fault) {
        if (fault instanceof HandlerException) {
            Throwable cause = ((HandlerException)fault).getCause();
            if (cause != null) {
                return unwrapFault(cause);
            }
        }
        if (fault instanceof InvocationTargetException) {
            Throwable cause = ((InvocationTargetException)fault).getCause();
            if (cause != null) {
                return unwrapFault(cause);
            }
        }
        return fault;
    }

    /**
     * Gets a fault message, if a fault exists.
     * @return the fault message
     */
    public String getFaultMessage() {
        String fmsg = null;
        Object fault = getFault();
        if (fault != null) {
            if (fault instanceof Throwable) {
                fmsg = String.format(CommonKnowledgeMessages.MESSAGES.faultEncountered()
                        + " [%s(message=%s)]: %s", fault.getClass().getName(), ((Throwable)fault).getMessage(), fault);
            } else {
                fmsg = String.format(CommonKnowledgeMessages.MESSAGES.faultEncountered()
                        + " [%s]: %s", fault.getClass().getName(), fault);
            }
        }
        return fmsg;
    }

    /**
     * Logs a fault message, if a fault exists.
     */
    public void logFaultMessage() {
        logFaultMessage(getFaultMessage());
    }

    /**
     * Logs a fault message, if specified.
     * @param fmsg the fault message
     */
    public void logFaultMessage(String fmsg) {
        if (fmsg != null) {
            CommonKnowledgeLogger.ROOT_LOGGER.formattedFaultMessage(fmsg);
        }
    }

}
