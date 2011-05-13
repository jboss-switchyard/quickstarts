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
     * @return True if the Java type is a match, otherwise false.
     */
    protected abstract boolean matches(Class<?> clazz);

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
