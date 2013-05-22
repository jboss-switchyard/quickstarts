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

import javax.xml.namespace.QName;

/**
 * SwitchYardServiceRequest.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SwitchYardServiceRequest {

    private final QName _serviceName;
    private final String _operationName;
    private final Object _content;
    private final Map<String, Object> _context = new HashMap<String, Object>();

    /**
     * Constructs a SwitchYardServiceRequest with the specified service name, service operation name, and content.
     * @param serviceName the service name
     * @param serviceOperationName the service operation name
     * @param content the content
     */
    public SwitchYardServiceRequest(QName serviceName, String serviceOperationName, Object content) {
        this(serviceName, serviceOperationName, content, null);
    }

    /**
     * Constructs a SwitchYardServiceRequest with the specified service name, operation name, content, and context.
     * @param serviceName the service name
     * @param operationName the operation name
     * @param content the content
     * @param context the context
     */
    public SwitchYardServiceRequest(QName serviceName, String operationName, Object content, Map<String, Object> context) {
        _serviceName = serviceName;
        _operationName = operationName;
        _content = content;
        if (context != null) {
            _context.putAll(context);
        }
    }

    /**
     * Gets the service name.
     * @return the service name
     */
    public QName getServiceName() {
        return _serviceName;
    }

    /**
     * Gets the operation name.
     * @return the operation name
     */
    public String getOperationName() {
        return _operationName;
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

}
