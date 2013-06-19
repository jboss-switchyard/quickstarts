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

package org.switchyard.deploy.osgi.internal;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.ServiceDomain;
import org.switchyard.admin.base.SwitchYardBuilder;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.osgi.ComponentRegistry;
import org.switchyard.deploy.osgi.NamespaceHandlerRegistry;
import org.switchyard.ProviderRegistry;
import org.switchyard.deploy.osgi.SwitchyardListener;
import org.switchyard.deploy.osgi.base.AbstractExtender;
import org.switchyard.deploy.osgi.base.CompoundExtension;
import org.switchyard.deploy.osgi.base.Extension;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class SwitchyardExtender extends AbstractExtender {

    public static final String SWITCHYARD_XML = "META-INF/switchyard.xml";

    private final Logger logger = LoggerFactory.getLogger(SwitchyardExtender.class);

    private NamespaceHandlerRegistry namespaceHandlerRegistry;

    private ComponentRegistry componentRegistry;

    private ProviderRegistryImpl providerRegistry;

    private OsgiDomainManager domainManager = new OsgiDomainManager(this);

    private SwitchyardEventDispatcher eventDispatcher;

    private Runnable management;

    @Override
    protected void doStart() throws Exception {
        try {
            management = Management.create(domainManager);
        } catch (NoClassDefFoundError e) {
            logger.warn("Management support disabled (package not available)");
        }
        eventDispatcher = new SwitchyardEventDispatcher(getBundleContext(), getExecutors());
        namespaceHandlerRegistry = new NamespaceHandlerRegistryImpl(getBundleContext());
        componentRegistry = new ComponentRegistryImpl(getBundleContext());
        providerRegistry = new ProviderRegistryImpl(getBundleContext());
        ProviderRegistry.setRegistry(providerRegistry);
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (management != null) {
            management.run();
        }
        ProviderRegistry.setRegistry(null);
        providerRegistry.destroy();
        componentRegistry.destroy();
        namespaceHandlerRegistry.destroy();
        eventDispatcher.destroy();
    }

    protected ExecutorService createExecutor() {
        return Executors.newScheduledThreadPool(3, new SwitchyardThreadFactory("Switchyard Extender"));
    }

    public NamespaceHandlerRegistry getNamespaceHandlerRegistry() {
        return namespaceHandlerRegistry;
    }

    public ComponentRegistry getComponentRegistry() {
        return componentRegistry;
    }

    public OsgiDomainManager getDomainManager() {
        return domainManager;
    }

    public SwitchyardListener getEventDispatcher() {
        return eventDispatcher;
    }

    @Override
    protected Extension doCreateExtension(Bundle bundle) throws Exception {
        // Check bundle compatibility
        if (!checkCompatible(bundle, ServiceDomain.class, Configuration.class)) {
            return null;
        }
        // Check switchyard extensions
        List<Extension> extensions = new ArrayList<Extension>();
        URL swCfg = bundle.getEntry(Descriptor.DEFAULT_PROPERTIES);
        if (swCfg != null) {
            extensions.add(new ConfigurationExtension(this, bundle));
        }
        URL swCmp = bundle.getEntry(ComponentExtension.META_INF_COMPONENT);
        if (swCmp != null) {
            extensions.add(new ComponentExtension(this, bundle));
        }
        URL swXml = bundle.getEntry(SWITCHYARD_XML);
        if (swXml != null) {
            extensions.add(new SwitchyardContainerImpl(this, bundle, getExecutors()));
        }
        return extensions.isEmpty() ? null : new CompoundExtension(bundle, extensions);
    }

    private boolean checkCompatible(Bundle bundle, Class... classes) {
        for (Class clazz : classes) {
            try {
                Class loaded = bundle.loadClass(clazz.getName());
                if (loaded != null && loaded != clazz) {
                    return false;
                }
            } catch (Throwable t) {
                // ignore if the class can' be loaded
            }
        }
        return true;
    }

    @Override
    protected void debug(Bundle bundle, String msg) {
        logger.debug("Switchyard extender for bundle " + bundle + ": " + msg);
    }

    @Override
    protected void warn(Bundle bundle, String msg, Throwable t) {
        logger.warn("Switchyard extender for bundle " + bundle + ": " + msg, t);
    }

    @Override
    protected void error(String msg, Throwable t) {
        logger.debug("Switchyard extender: " + msg, t);
    }

    static class Management {
        public static Runnable create(ServiceDomainManager manager) {
            final SwitchYardBuilder builder = new SwitchYardBuilder();
            builder.init(manager);
            return new Runnable() {
                @Override
                public void run() {
                    builder.destroy();
                }
            };
        }
    }
}
