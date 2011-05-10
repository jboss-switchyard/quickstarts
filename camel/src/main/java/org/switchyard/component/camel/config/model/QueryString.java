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

package org.switchyard.component.camel.config.model;

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
