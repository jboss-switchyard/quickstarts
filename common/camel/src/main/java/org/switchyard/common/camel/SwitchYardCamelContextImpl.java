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

import org.apache.camel.cdi.CdiBeanRegistry;
import org.apache.camel.cdi.CdiInjector;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.EventNotifier;
import org.apache.camel.spi.PackageScanClassResolver;
import org.apache.camel.spi.Registry;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.event.CamelEventBridge;
import org.switchyard.common.cdi.CDIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extension of default camel context. Supports access to mutable registry and
 * provides integration with SwitchYard eventing model.
 */
public class SwitchYardCamelContextImpl extends DefaultCamelContext implements SwitchYardCamelContext {

    private final SimpleRegistry _writeableRegistry = new SimpleRegistry();
    private ServiceDomain _domain;

    private AtomicInteger _count = new AtomicInteger();

    private static final int DEFAULT_TIMEOUT = 30;

    /**
     * Flag to turn on/off cdi integration.
     */
    private boolean _cdiIntegration;

    /**
     * Creates new camel context.
     */
    public SwitchYardCamelContextImpl() {
        this(true);
    }

    /**
     * Creates new camel context.
     * @param autoDetectCdi Should cdi integration be auto detected and enabled.
     */
    public SwitchYardCamelContextImpl(boolean autoDetectCdi) {
        _cdiIntegration = autoDetectCdi;
        if (isEnableCdiIntegration()) {
            CDISupport.setCdiInjector(this);
        } else {
        	CommonCamelLogger.ROOT_LOGGER.cdiNotDetected();
        }
        getManagementStrategy().addEventNotifier(new CamelEventBridge());
    }

    /**
     * Associates camel context with given service domain.
     * 
     * @param domain Domain to associate.
     */
    public void setServiceDomain(ServiceDomain domain) {
        this._domain = domain;

        for (EventNotifier notifier : getManagementStrategy().getEventNotifiers()) {
            if (notifier instanceof CamelEventBridge) {
                ((CamelEventBridge) notifier).setEventPublisher(domain.getEventPublisher());
            }
        }

        PackageScanClassResolver packageScanClassResolver = findPackageScanClassResolver();
        if (packageScanClassResolver != null) {
            setPackageScanClassResolver(packageScanClassResolver);
        }

        _domain.setProperty(CAMEL_CONTEXT_PROPERTY, this);
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
    public PackageScanClassResolver findPackageScanClassResolver() {
        final ServiceLoader<PackageScanClassResolver> resolverLoaders = ServiceLoader.load(PackageScanClassResolver.class);

        for (PackageScanClassResolver packageScanClassResolver : resolverLoaders) {
            return packageScanClassResolver;
        }

        return null;
    }

    protected CompositeRegistry createRegistry() {
        final ServiceLoader<Registry> registriesLoaders = ServiceLoader.load(Registry.class, getClass().getClassLoader());

        final List<Registry> registries = new ArrayList<Registry>();
        registries.add(new JndiRegistry());
        if (isEnableCdiIntegration()) {
        	registries.add(new CdiBeanRegistry());
        }
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
     * Checks if CDI runtime is enabled for this deployment.
     * 
     * @return True if CDI runtime is detected.
     */
    public boolean isEnableCdiIntegration() {
        if (!_cdiIntegration) {
            return false;
        }
        return CDISupport.isCDIEnabled();
    }

    /**
     * Start camel context and/or increment counter with number of start attempts.
     * 
     * @throws Exception is thrown if starting failed
     */
    @Override
    public void start() throws Exception {
        if (_count.incrementAndGet() == 1) {
            applyConfiguration();
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

    // Applies domain configuration to this camel context
    private void applyConfiguration() {
        if (_domain == null || _domain.getProperties() == null) {
            // no config to apply
            return;
        }
        
        // set shutdown timeout
        Object timeoutProp = _domain.getProperty(SHUTDOWN_TIMEOUT);
        int timeout;
        if (timeoutProp != null) {
            timeout = Integer.parseInt(timeoutProp.toString());
        } else {
            timeout = DEFAULT_TIMEOUT;
        }
        getShutdownStrategy().setTimeout(timeout);
    }

    static class CDISupport {

        static boolean isCDIEnabled() {
            try {
                return CDIUtil.lookupBeanManager() != null;
            } catch (NoClassDefFoundError e) {
                return false;
            }
        }

        static void addCdiRegistry(List<Registry> registries) {
            registries.add(new CdiBeanRegistry());
        }

        public static void setCdiInjector(SwitchYardCamelContextImpl context) {
            context.setInjector(new CdiInjector(context.getInjector()));
        }
    }

}
