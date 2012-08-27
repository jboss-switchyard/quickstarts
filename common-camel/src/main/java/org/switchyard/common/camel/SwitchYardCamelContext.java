/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.common.camel;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.PackageScanClassResolver;
import org.apache.camel.spi.Registry;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.event.CamelEventBridge;
import org.switchyard.event.EventPublisher;

/**
 * Extension of default camel context. Supports access to mutable registry and
 * provides integration with SwitchYard eventing model.
 */
public class SwitchYardCamelContext extends DefaultCamelContext {

    /**
     * Context property name used to store camel context as service domain property.
     */
    public static final String CAMEL_CONTEXT_PROPERTY = "CamelContextProperty";

    private final SimpleRegistry _writeableRegistry = new SimpleRegistry();
    private ServiceDomain _domain;

    private AtomicInteger _count = new AtomicInteger();

    /**
     * Associates camel context with given service domain.
     * 
     * @param domain Domain to associate.
     */
    public void setServiceDomain(ServiceDomain domain) {
        this._domain = domain;

        EventPublisher eventPublisher = domain.getEventPublisher();
        getManagementStrategy().addEventNotifier(new CamelEventBridge(eventPublisher));

        PackageScanClassResolver packageScanClassResolver = getPackageScanClassResolver();
        if (packageScanClassResolver != null) {
            setPackageScanClassResolver(packageScanClassResolver);
        }

        this._domain.getProperties().put(CAMEL_CONTEXT_PROPERTY, this);
    }

    /**
     * Gets mutable registry associated with context. Allows to dynamically register
     * bean instances.
     * 
     * @return Registry which allows to put new objects.
     */
    public SimpleRegistry getWritebleRegistry() {
        return _writeableRegistry;
    }

    /**
     * Get the first PackageScanClassResolver Service found on the classpath.
     * 
     * @return The first PackageScanClassResolver Service found on the classpath.
     */
    public PackageScanClassResolver getPackageScanClassResolver() {
        final ServiceLoader<PackageScanClassResolver> resolverLoaders = ServiceLoader
            .load(PackageScanClassResolver.class, this.getClass().getClassLoader());

        for (PackageScanClassResolver packageScanClassResolver : resolverLoaders) {
            return packageScanClassResolver;
        }

        return null;
    }

    protected CompositeRegistry createRegistry() {
        final ServiceLoader<Registry> registriesLoaders = ServiceLoader
            .load(Registry.class, getClass().getClassLoader());

        final List<Registry> registries = new ArrayList<Registry>();
        registries.add(new JndiRegistry());
        registries.add(_writeableRegistry);

        for (Registry registry : registriesLoaders) {
            registries.add(registry);
        }

        return new CompositeRegistry(registries);
    }

    /**
     * Gets SwitchYard domain associated with this context.
     * 
     * @return SwitchYard domain.
     */
    public ServiceDomain getServiceDomain() {
        return _domain;
    }

    /**
     * Start camel context and/or increment counter with number of start attempts.
     * 
     * @throws Exception is thrown if starting failed
     */
    @Override
    public void start() throws Exception {
        if (_count.incrementAndGet() == 1) {
            super.start();
        }
    }

    /**
     * Decrement counter with number of start attempts and/or stop camel context.
     * 
     * @throws Exception is thrown if stopping failed
     */
    @Override
    public void stop() throws Exception {
        if (_count.decrementAndGet() == 0) {
            super.stop();
        }
    }

}
