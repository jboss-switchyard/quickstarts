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
        return propertyName.startsWith("org.switchyard.bus.camel")
                || propertyName.startsWith("org.switchyard.component.camel");
    }
}
