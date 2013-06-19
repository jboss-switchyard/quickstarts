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
package org.switchyard;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 */
public final class ProviderRegistry {

    private ProviderRegistry() {
    }

    /**
     * Obtain a provider
     *
     * @param clazz the class of the provider to find
     * @param <T> the provider type
     * @return a provider of the specified class or null if none is available
     */
    public static <T> T getProvider(Class<T> clazz) {
        if (registry != null) {
            return registry.getProvider(clazz);
        }
        ServiceLoader<T> services = ServiceLoader.load(clazz, clazz.getClassLoader());
        Iterator<T> iterator = services.iterator();
        while (iterator.hasNext()) {
            try {
                return iterator.next();
            } catch (Throwable t) {
                _logger.debug("Error creating provider for class " + clazz.getName(), t);
            }
        }
        return null;
    }

    /**
     * Obtain a list of providers
     *
     * @param clazz the class of the provider to find
     * @param <T> the provider type
     * @return the list of providers found
     */
    public static <T> List<T> getProviders(Class<T> clazz) {
        if (registry != null) {
            return registry.getProviders(clazz);
        }
        List<T> list = new ArrayList<T>();
        ServiceLoader<T> services = ServiceLoader.load(clazz, clazz.getClassLoader());
        Iterator<T> iterator = services.iterator();
        while (iterator.hasNext()) {
            try {
                list.add(iterator.next());
            } catch (Throwable t) {
                _logger.debug("Error creating provider for class " + clazz.getName(), t);
            }
        }
        return list;
    }

    private static Registry registry;
    private static Logger _logger = Logger.getLogger(ProviderRegistry.class);

    public static void setRegistry(Registry registry) {
        ProviderRegistry.registry = registry;
    }

    public interface Registry {
        <T> T getProvider(Class<T> clazz);
        <T> List<T> getProviders(Class<T> clazz);
    }

}
