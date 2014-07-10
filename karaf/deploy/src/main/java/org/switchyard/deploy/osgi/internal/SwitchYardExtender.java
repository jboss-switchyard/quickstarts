/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.deploy.osgi.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.ServiceDomain;
import org.switchyard.admin.SwitchYard;
import org.switchyard.admin.base.SwitchYardBuilder;
import org.switchyard.common.util.ProviderRegistry;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.osgi.ComponentRegistry;
import org.switchyard.deploy.osgi.NamespaceHandlerRegistry;
import org.switchyard.deploy.osgi.SwitchYardListener;
import org.switchyard.deploy.osgi.TransformSource;
import org.switchyard.deploy.osgi.base.AbstractExtender;
import org.switchyard.deploy.osgi.base.CompoundExtension;
import org.switchyard.deploy.osgi.base.Extension;

/**
 * SwitchYardExtender.
 */
public class SwitchYardExtender extends AbstractExtender {

    public static final String SWITCHYARD_XML = "META-INF/switchyard.xml";
    public static final String WEBINF_SWITCHYARD_XML = "WEB-INF/switchyard.xml";
    
    private final Logger _logger = LoggerFactory.getLogger(SwitchYardExtender.class);

    private NamespaceHandlerRegistry _namespaceHandlerRegistry;

    private ComponentRegistry _componentRegistry;

    private ProviderRegistryImpl _providerRegistry;

    private OsgiDomainManager _domainManager = new OsgiDomainManager(this);

    private SwitchYardEventDispatcher _eventDispatcher;

    private Runnable _management;
    
    private List<ServiceRegistration<TransformSource>> _transformSources = 
            new LinkedList<ServiceRegistration<TransformSource>>();

    @Override
    protected void doStart() throws Exception {
        try {
            _management = createManagement(_domainManager);
        } catch (NoClassDefFoundError e) {
            _logger.warn("Management support disabled (package not available)", e);
        }
        _eventDispatcher = new SwitchYardEventDispatcher(getBundleContext(), getExecutors());
        _namespaceHandlerRegistry = new NamespaceHandlerRegistryImpl(getBundleContext());
        _componentRegistry = new ComponentRegistryImpl(getBundleContext());
        _providerRegistry = new ProviderRegistryImpl(getBundleContext());
        ProviderRegistry.setRegistry(_providerRegistry);
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (_management != null) {
            _management.run();
            _management = null;
        }
        
        // remove TransformSource registrations
        for (ServiceRegistration<?> reg : _transformSources) {
            try {
                reg.unregister();
            } catch (Exception ex) {
                // Exceptions here are not fatal and we see a lot of IllegalStateException
                // if the services have already been unregistered - drop to DEBUG log
                // probably becuase the contributing bundle stopped
                _logger.debug("Error while unregistering TransformSource service", ex);
            }
        }
        _transformSources.clear();
        
        ProviderRegistry.setRegistry(null);
        _providerRegistry.destroy();
        _componentRegistry.destroy();
        _namespaceHandlerRegistry.destroy();
        _eventDispatcher.destroy();
    }

    protected ExecutorService createExecutor() {
        return Executors.newScheduledThreadPool(3, new SwitchYardThreadFactory("Switchyard Extender"));
    }

    public NamespaceHandlerRegistry getNamespaceHandlerRegistry() {
        return _namespaceHandlerRegistry;
    }

    public ComponentRegistry getComponentRegistry() {
        return _componentRegistry;
    }

    public OsgiDomainManager getDomainManager() {
        return _domainManager;
    }

    public SwitchYardListener getEventDispatcher() {
        return _eventDispatcher;
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
        if (swXml == null) {
            swXml = bundle.getEntry(WEBINF_SWITCHYARD_XML);
        }
        if (swXml != null) {
            extensions.add(new SwitchYardContainerImpl(this, bundle, getExecutors()));
        }
        URL tfXml = bundle.getEntry(TransformSource.TRANSFORMS_XML);
        if (tfXml != null) {
            TransformSource trs = new TransformSourceImpl(bundle);
            ServiceRegistration<TransformSource> trsService = bundle.getBundleContext()
                    .registerService(TransformSource.class, trs, null);
            _transformSources.add(trsService);
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
        _logger.debug("Switchyard extender for bundle " + bundle + ": " + msg);
    }

    @Override
    protected void warn(Bundle bundle, String msg, Throwable t) {
        _logger.warn("Switchyard extender for bundle " + bundle + ": " + msg, t);
    }

    @Override
    protected void error(String msg, Throwable t) {
        _logger.debug("Switchyard extender: " + msg, t);
    }

    private Runnable createManagement(ServiceDomainManager manager) {
        final SwitchYardBuilder builder = new SwitchYardBuilder();
        builder.init(manager);
        final ServiceRegistration<SwitchYard> reg = getBundleContext().registerService(SwitchYard.class,
                builder.getSwitchYard(), null);
        return new Runnable() {
            @Override
            public void run() {
                reg.unregister();
                builder.destroy();
            }
        };
    }
}
