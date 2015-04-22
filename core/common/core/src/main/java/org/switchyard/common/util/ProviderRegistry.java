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
package org.switchyard.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.jboss.logging.Logger;
import org.switchyard.common.type.Classes;

/**
 * A registry for service providers which allows for service lookup using a
 * registry in place of ServiceLoader. If no registry is available,
 * ServiceLoader is used as a fallback.
 */
public final class ProviderRegistry {

    private static Registry _registry;
    private static Logger _logger = Logger.getLogger(ProviderRegistry.class);

    private ProviderRegistry() {
    }

    /**
     * Obtain a provider using the Thread Context ClassLoader.
     * 
     * @param clazz the class of the provider to find
     * @param <T> the provider type
     * @return a provider of the specified class or null if none is available
     */
    public static <T> T getProvider(Class<T> clazz) {
        return getProvider(clazz, Classes.getTCCL());
    }

    /**
     * Obtain a provider using a specified ClassLoader.
     * 
     * @param clazz the class of the provider to find
     * @param loader the class loader
     * @param <T> the provider type
     * @return a provider of the specified class or null if none is available
     */
    public static <T> T getProvider(Class<T> clazz, ClassLoader loader) {
        if (_registry != null) {
            return _registry.getProvider(clazz);
        }
        ServiceLoader<T> services = ServiceLoader.load(clazz, loader);
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
     * Obtain a list of providers using the Thread Context ClassLoader.
     * 
     * @param clazz the class of the provider to find
     * @param <T> the provider type
     * @return the list of providers found
     */
    public static <T> List<T> getProviders(Class<T> clazz) {
        return getProviders(clazz, Classes.getTCCL());
    }

    /**
     * Obtain a list of providers using a specified ClassLoader.
     * 
     * @param clazz the class of the provider to find
     * @param loader the class loader
     * @param <T> the provider type
     * @return the list of providers found
     */
    public static <T> List<T> getProviders(Class<T> clazz, ClassLoader loader) {
        if (_registry != null) {
            return _registry.getProviders(clazz);
        }
        List<T> list = new ArrayList<T>();
        ServiceLoader<T> services = ServiceLoader.load(clazz, loader);
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

    /**
     * Set a registry provider to be used for service lookups.
     * @param registry service provider registry
     */
    public static void setRegistry(Registry registry) {
        ProviderRegistry._registry = registry;
    }

    /**
     * Represents a pluggable service registry provider.
     */
    public interface Registry {
        /**
         * Return a provider for the specified interface.
         * @param clazz the class of the provider to find
         * @param <T> the provider type
         * @return provider
         */
        <T> T getProvider(Class<T> clazz);

        /**
         * Return a list of providers for the specified interface.
         * @param clazz the class of the provider to find
         * @param <T> the provider type
         * @return list of providers
         */
        <T> List<T> getProviders(Class<T> clazz);
    }

}
