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
package org.switchyard.component.common.knowledge.service;

import java.util.HashMap;
import java.util.Map;

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
     * Gets the fault, if it exists.
     * @return the fault, or null if it doesn't exist
     */
    public Object getFault() {
        return _fault;
    }

    /**
     * If a fault exists.
     * @return if a fault exists
     */
    public boolean hasFault() {
        return getFault() != null;
    }

}
