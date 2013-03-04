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
package org.switchyard.component.resteasy.composer;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.switchyard.component.common.composer.BindingData;

/**
 * Wrapper for RESTEasy messages.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> &copy; 2012 Red Hat Inc.
 */
public class RESTEasyBindingData implements BindingData {
    private Object[] _args = new Object[0];
    private MultivaluedMap<String, String> _headers;
    private String _operationName;

    /**
     * Creates a new RESTEasy message.
     */
    public RESTEasyBindingData() {}

    /**
     * Creates a new RESTEasy message, given the specified content.
     * @param content the specified content
     */
    public RESTEasyBindingData(Object content) {
        _args = new Object[]{content};
    }

    /**
     * Gets the operation name to be invoked.
     * @return the operations name
     */
    public String getOperationName() {
        return _operationName;
    }

    /**
     * Sets the operation name to be invoked.
     * @param operationName the operations name
     */
    public void setOperationName(String operationName) {
        _operationName = operationName;
    }

    /**
     * Gets the method parameters as passed from RESTEasy.
     * @return the method parameters
     */
    public Object[] getParameters() {
        return _args;
    }

    /**
     * Sets the method parameters.
     * @param args the method parameters
     */
    public void setParameters(Object[] args) {
        if (args != null) {
            _args = args;
        }
    }

    /**
     * Get the HTTP headers map.
     * @return a Map of headers
     */
    public MultivaluedMap<String, String> getHeaders() {
        if (_headers == null) {
            _headers = new CaseInsensitiveMap<String>();
        }
        return _headers;
    }

    /**
     * Set the HTTP headers map.
     * @param headers a Map of headers
     */
    public void setHeaders(MultivaluedMap<String, String> headers) {
        _headers = headers;
    }

    /**
     * Add a HTTP header.
     * @param name the name of the header
     * @param values a List of header values
     */
    public void addHeader(String name, List<String> values) {
        if (_headers == null) {
            _headers = new CaseInsensitiveMap<String>();
        }
        _headers.put(name, values);
    }

    /**
     * Add a HTTP header.
     * @param name the name of the header
     * @param value a header value
     */
    public void addHeader(String name, String value) {
        if (_headers == null) {
            _headers = new CaseInsensitiveMap<String>();
        }
        List<String> values = _headers.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            _headers.put(name, values);
        }
        values.add(value);
    }
}
