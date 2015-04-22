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
