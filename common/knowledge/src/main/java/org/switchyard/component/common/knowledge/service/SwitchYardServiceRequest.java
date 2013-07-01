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
