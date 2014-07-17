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
package org.switchyard.component.sca.deploy;

import java.io.InputStream;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.logging.Logger;
import org.switchyard.component.sca.RemoteEndpointPublisher;
import org.switchyard.component.sca.SCAEndpoint;
import org.switchyard.component.sca.SCAInvoker;
import org.switchyard.component.sca.SCALogger;
import org.switchyard.component.sca.SCAMessages;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.SCABindingModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.remote.RemoteRegistry;
import org.switchyard.remote.infinispan.InfinispanRegistry;

/**
 * Activator for remote endpoints.
 *
 */
public class SCAActivator extends BaseActivator {
    
    private static final String CACHE_CONTAINER_ROOT = "java:jboss/infinispan/container/";
    private static final String CACHE_NAME_PROPERTY = "cache-name";
    private static final String CACHE_CONFIG_PROPERTY = "cache-config";
    static final String[] TYPES = new String[] {"sca"};

    private static Logger _log = Logger.getLogger(SCAActivator.class);
    private Cache<String, String> _cache;
    private RemoteRegistry _registry;
    private RemoteEndpointPublisher _endpointPublisher;
    
    /**
     * Create a new RemoteActivator.
     * @param environment component configuration used by the activator
     */
    public SCAActivator(Configuration environment) {
        super(TYPES);
        
        // Determine the cache name
        String cacheName = "cluster";
        Configuration cacheNameConfig = environment.getFirstChild(CACHE_NAME_PROPERTY);
        if (cacheNameConfig != null) {
            cacheName = cacheNameConfig.getValue();
        }
        
        // If a cache config is specified in the component configuration, then we 
        // are managing the creation of the cache.  If not, then we look up a 
        // cache manager and try to resolve the cache.
        Configuration cacheFileConfig = environment.getFirstChild(CACHE_CONFIG_PROPERTY);
        if (cacheFileConfig != null && cacheFileConfig.getValue() != null) {
            createCache(cacheFileConfig.getValue(), cacheName);
        } else {
            lookupCache(cacheName);
        }
        
        // Configure a registry client to use the specified cache
        if (_cache != null) {
            _registry = new InfinispanRegistry(_cache);
        } else {
            SCALogger.ROOT_LOGGER.unableToResolveCacheContainer(cacheName);
        }
    }
    
    @Override
    public ServiceHandler activateBinding(QName name, BindingModel config) {
        // Signal the remote endpoint publisher to start. Multiple calls to start are harmless.
        try {
            // Note that stop() occurs as part of the SCAComponent lifecycle.
            _endpointPublisher.start();
        } catch (Exception ex) {
            SCALogger.ROOT_LOGGER.failedToStartRemoteEndpointListenerForSCAEndpoints(ex);
        }
        
        SCABindingModel scab = (SCABindingModel)config;
        if (scab.isServiceBinding()) {
            return new SCAEndpoint(scab, super.getServiceDomain(), _endpointPublisher, _registry);
        } else {
            if ((scab.getTarget() == null) && (scab.getTargetNamespace() == null)) {
                throw SCAMessages.MESSAGES.invalidSCABindingForReferenceTargetServiceOrNamespaceMustBeSpecified();
            }
            return new SCAInvoker(scab, _registry);
        }
    }

    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        // Nothing to do here
    }
    
    /**
     * Set the endpoint publisher used by this activator.
     * @param endpointPublisher endpoint publisher
     */
    public void setEndpointPublisher(RemoteEndpointPublisher endpointPublisher) {
        _endpointPublisher = endpointPublisher;
    }
    
    /**
     * Get the endpoint publisher used by this activator.
     * @return endpoint publisher
     */
    public RemoteEndpointPublisher getEndpointPublisher() {
        return _endpointPublisher;
    }
    
    private void createCache(String cacheConfig, String cacheName) {
        ClassLoader origCl = Thread.currentThread().getContextClassLoader();
        try {
            InputStream configStream = SCAActivator.class.getClassLoader().getResourceAsStream(cacheConfig);
            Thread.currentThread().setContextClassLoader(DefaultCacheManager.class.getClassLoader());
            _cache = new DefaultCacheManager(configStream).getCache(cacheName);
        } catch (Exception ex) {
            _log.debug("Failed to create cache for distributed registry", ex);
        } finally {
            Thread.currentThread().setContextClassLoader(origCl);
        }
    }
    
    private void lookupCache(String cacheName) {
        // Attempt to resolve the cache container to use for the distributed registry through JNDI
        try {
            EmbeddedCacheManager cm = (EmbeddedCacheManager)
                    new InitialContext().lookup(CACHE_CONTAINER_ROOT + cacheName);
            _cache = cm.getCache();
        } catch (Exception ex) {
            _log.debug("Failed to lookup cache container for distributed registry", ex);
        }
    }
}
