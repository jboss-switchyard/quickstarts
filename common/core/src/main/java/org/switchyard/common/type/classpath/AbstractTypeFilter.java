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

package org.switchyard.common.type.classpath;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.switchyard.common.type.Classes;

/**
 * Abstract Java type filter.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class AbstractTypeFilter implements Filter {

    private Logger _logger;
    private List<Class<?>> _classes = new ArrayList<Class<?>>();

    /**
     * Protected constructor.
     */
    protected AbstractTypeFilter() {
        _logger = Logger.getLogger(getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean continueScanning() {
        return true;
    }

    /**
     * Is the Java type a filter match.
     * @param clazz The Java type to be checked.
     * @return true if the Java type is a match, otherwise false.
     */
    public abstract boolean matches(Class<?> clazz);

    /**
     * Get the set of filtered (i.e. matching) types.
     * @return The set of filtered (i.e. matching) types.
     */
    public List<Class<?>> getMatchedTypes() {
        return _classes;
    }

    /**
     * Clear the current set of matched types.
     */
    public void clear() {
        _classes.clear();
    }

    /**
     * Filter the specified resource.
     * @param resourceName The classpath resource file name.
     */
    public void filter(String resourceName) {
        if (resourceName.endsWith(".class")) {
            String className = toClassName(resourceName);

            try {
                // Assumption here is that these classes are on the scanner's classpath...
                Class<?> clazz = Classes.forName(className, getClass());
                if (matches(clazz)) {
                    _classes.add(clazz);
                }
            } catch (Throwable throwable) {
                _logger.debug("Resource '" + resourceName + "' presented to '" + InstanceOfFilter.class.getName() + "', but not loadable by classloader.  Ignoring.", throwable);
            }
        }
    }

    private String toClassName(String resourceName) {
        if (resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
        }
        if (resourceName.endsWith(".class")) {
            resourceName = resourceName.substring(0, resourceName.length() - ".class".length());
        }
        resourceName = resourceName.replace('/', '.');
        return resourceName;
    }
}
