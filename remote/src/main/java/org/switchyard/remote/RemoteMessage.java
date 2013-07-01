/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
