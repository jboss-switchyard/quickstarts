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
package org.switchyard.common.camel;

import org.switchyard.Scope;

/**
 * Utility class for filtering system reserved properties from exchange and message headers.
 */
public final class ContextPropertyUtil {

    private ContextPropertyUtil() {
        
    }

    /**
     * Utility method to verify if given property name is reserved for internal
     * usage.
     * 
     * @param propertyName Name of camel exchange property or header.
     * @param scope Scope of the property.
     * @return True if property is used internally.
     */
    public static boolean isReservedProperty(String propertyName, Scope scope) {
        return propertyName.startsWith("org.switchyard.bus.camel");
    }
}
