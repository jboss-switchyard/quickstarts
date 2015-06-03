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
package org.switchyard.remote;

import javax.xml.namespace.QName;

import org.switchyard.Context;
import org.switchyard.internal.CompositeContext;
import org.switchyard.serial.graph.AccessType;
import org.switchyard.serial.graph.Strategy;

/**
 * Serializable container for context and content used in an invocation.
 */
@Strategy(access=AccessType.FIELD)
public class RemoteMessage {

    private Context _context;
    private Object _content;
    private QName _service;
    private QName _domain;
    private String _operation;
    private boolean _fault;
    
    /**
     * Create an empty remote message.
     */
    public RemoteMessage() {
        _context = new CompositeContext();
    }
    
    /**
     * Create a remote message for the specified domain and service.
     * @param domain the domain name
     * @param service the service name
     */
    public RemoteMessage(QName domain, QName service) {
        _domain = domain;
        _service = service;
    }
    
    /**
     * Returns the context properties for the invocation.
     * @return context properties
     */
    public Context getContext() {
        return _context;
    }
    
    /**
     * Returns the content for the invocation.
     * @return the message content
     */
    public Object getContent() {
        return _content;
    }
    
    /**
     * Specifies the content for the invocation.
     * @param content message content
     * @return a reference to this RemoteMessage
     */
    public RemoteMessage setContent(Object content) {
        _content = content;
        return this;
    }
    
    /**
     * Returns the service name for the invocation.
     * @return the service name
     */
    public QName getService() {
        return _service;
    }
    
    /**
     * Specifies the name of the service being invoked.
     * @param service service name
     * @return a reference to this RemoteMessage
     */
    public RemoteMessage setService(QName service) {
        _service = service;
        return this;
    }
    
    /**
     * Returns the name of the domain for the invocation.
     * @return a reference to this RemoteMessage
     */
    public QName getDomain() {
        return _domain;
    }
    
    /**
     * Specifies the name of the domain for the invocation.
     * @param domain domain name
     * @return a reference to this RemoteMessage
     */
    public RemoteMessage setDomain(QName domain) {
        _domain = domain;
        return this;
    }
    
    /**
     * Returns the operation being invoked.
     * @return operation name
     */
    public String getOperation() {
        return _operation;
    }
    
    /**
     * Specifies the name of the operation being invoked.
     * @param operation operation name
     * @return a reference to this RemoteMessage
     */
    public RemoteMessage setOperation(String operation) {
        _operation = operation;
        return this;
    }
    
    /**
     * Specifies whether this remote message represents a fault.
     * @param isFault true if this is a fault, false otherwise
     * @return a reference to this RemoteMessage
     */
    public RemoteMessage setFault(boolean isFault) {
        _fault = isFault;
        return this;
    }
    
    /**
     * Indicates whether this remote message represents a fault.
     * @return true if this is a fault, false otherwise
     */
    public boolean isFault() {
        return _fault;
    }
}
