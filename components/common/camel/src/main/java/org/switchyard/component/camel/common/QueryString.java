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
package org.switchyard.component.camel.common;

/**
 * Represents the query string portion of a URI.  The query string is built
 * by adding name value pairs.  Calling <code>toString()</code> produces a 
 * string which can be appended to the root (scheme + host) portion of a URI.
 */
public class QueryString {
    
    private StringBuilder _uriStr;

    /**
     * Creates a new QueryString.
     */
    public QueryString() {
        _uriStr = new StringBuilder();
    }
    
    /**
     * Add a name/value pair to the query string.
     * @param name parameter name
     * @param value parameter value
     * @return a reference to this QueryString to promote fluent builder 
     */
    public QueryString add(String name, Object value) {
        if (name != null && value != null) {
            if (_uriStr.length() > 0) {
                // we already have a query parameter, so add a separator
                _uriStr.append('&');
            }
            _uriStr.append(name + "=" + value);
        }
        return this;
    }
    
    /**
     * Returns a String representation of the query string.  If no parameters
     * have been added then an empty string is returned.
     * @return string version of a URI
     */
    public String toString() {
        if (_uriStr.length() == 0) {
            return "";
        } else {
            return "?" + _uriStr.toString();
        }
    }
}
