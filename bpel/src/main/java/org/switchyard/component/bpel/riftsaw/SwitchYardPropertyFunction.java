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
package org.switchyard.component.bpel.riftsaw;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

import org.switchyard.common.property.PropertyResolver;

/**
 * The custom XPath function which resolves SwitchYard properties referenced from BPEL process definition.
 */
public final class SwitchYardPropertyFunction {

    private static final Map<QName, PropertyResolver> PROPERTY_RESOLVERS = new HashMap<QName, PropertyResolver>();
    
    private SwitchYardPropertyFunction() {}
    
    /**
     * Sets PropertyResolver instance.
     * @param processName BPEL process QName
     * @param resolver PropertyResolver instance
     */
    public static final void setPropertyResolver(QName processName, PropertyResolver resolver) {
        PROPERTY_RESOLVERS.put(processName, resolver);
    }

    /**
     * Removes a PropertyResolver for the specified process.
     * @param processName BPEL process QName
     */
    public static final void removePropertyResolver(QName processName) {
        PROPERTY_RESOLVERS.remove(processName);
    }
    
    /**
     * Resolves a property.
     * @param processName BPEL process QName
     * @param key property name
     * @return property value
     */
    public static final Object resolveProperty(QName processName, Object key) {
        PropertyResolver resolver = PROPERTY_RESOLVERS.get(processName);
        if (resolver == null) {
            return null;
        }
        return resolver.resolveProperty(key.toString());
    }
}
