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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.naming.InitialContext;

import org.infinispan.Cache;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.configuration.parsing.ParserRegistry;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.logging.Logger;
import org.switchyard.ServiceDomain;
import org.switchyard.common.util.ProviderRegistry;
import org.switchyard.component.sca.NOPEndpointPublisher;
import org.switchyard.component.sca.RemoteEndpointPublisher;
import org.switchyard.component.sca.SCALogger;
import org.switchyard.config.Configuration;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.BaseComponent;

/**
 * The "remote" component used in SwitchYard for providing and consuming clustered endpoints.
 */
public class SCAComponent extends BaseComponent {
    
    private static final String CONTEXT_PATH = "switchyard-remote";
    private static final String CACHE_CONTAINER_ROOT = "java:jboss/infinispan/container/";
    private static final String CACHE_NAME_PROPERTY = "cache-name";
    private static final String CACHE_CONFIG_PROPERTY = "cache-config";
    private static final String JGROUPS_CONFIG_PROPERTY = "jgroups-config";
    private static final String DISABLE_REMOTE_TRANSACTION_PROPERTY = "disable-remote-transaction";

    private Logger _log = Logger.getLogger(SCAComponent.class);
    private RemoteEndpointPublisher _endpointPublisher;
    private String _cacheName;
    private Cache<String, String> _cache;
    private boolean _disableRemoteTransaction = false;

    /**
     * Default constructor.
     */
    public SCAComponent() {
        super(SCAActivator.TYPES);
        setName("RemoteComponent");
        try {
            initEndpointPublisher();
        } catch (Exception ex) {
            SCALogger.ROOT_LOGGER.failedToInitializeRemoteEndpointPublisher(ex);
        }
    }
    
    @Override
    public void init(Configuration environment) {
        super.init(environment);

        // Determine the cache name
        _cacheName = "cluster";
        Configuration cacheNameConfig = environment.getFirstChild(CACHE_NAME_PROPERTY);
        if (cacheNameConfig != null) {
            _cacheName = cacheNameConfig.getValue();
        }
        
        // If a cache config is specified in the component configuration, then we 
        // are managing the creation of the cache.  If not, then we look up a 
        // cache manager and try to resolve the cache.
        Configuration cacheFileConfig = environment.getFirstChild(CACHE_CONFIG_PROPERTY);
        if (cacheFileConfig != null && cacheFileConfig.getValue() != null) {
            Configuration jgroupsConfig = environment.getFirstChild(JGROUPS_CONFIG_PROPERTY);
            createCache(cacheFileConfig.getValue(), _cacheName, jgroupsConfig != null ? jgroupsConfig.getValue() : null);
        }
        
        Configuration bridgeRemoteTxConfig = environment.getFirstChild(DISABLE_REMOTE_TRANSACTION_PROPERTY);
        if (bridgeRemoteTxConfig != null) {
            _disableRemoteTransaction = Boolean.parseBoolean(bridgeRemoteTxConfig.getValue());
        }
    }
    
    @Override
    public Activator createActivator(ServiceDomain domain) {
        if (_cache == null) {
            lookupCache(_cacheName);
            if (_cache == null) {
                SCALogger.ROOT_LOGGER.unableToResolveCacheContainer(_cacheName);
            }
        }

        SCAActivator activator = new SCAActivator(_cache);
        activator.setServiceDomain(domain);
        activator.setEndpointPublisher(_endpointPublisher);
        activator.setDisableRemoteTransaction(_disableRemoteTransaction);
        return activator;
    }

    @Override
    public synchronized void destroy() {
        try {
            _endpointPublisher.stop();
        } catch (Exception ex) {
            SCALogger.ROOT_LOGGER.failedToDestroyRemoteEndpointPublisher(ex);
        }
    }
    
    private void initEndpointPublisher() {
        _endpointPublisher = ProviderRegistry.getProvider(RemoteEndpointPublisher.class);
        // if we couldn't find a provider, default to NOP implementation
        if (_endpointPublisher == null) {
            _endpointPublisher = new NOPEndpointPublisher();
            SCALogger.ROOT_LOGGER.noEndpointPublisherRegistered();
        }
        _endpointPublisher.init(CONTEXT_PATH);
    }

    private void createCache(String cacheConfig, String cacheName, String jgroupsConfig) {
        ClassLoader origCl = Thread.currentThread().getContextClassLoader();
        try {
            InputStream configStream = null;
            try {
                File f = new File(cacheConfig);
                if (f.exists() && f.isFile()) {
                    configStream = new FileInputStream(f);
                } else {
                    configStream = SCAActivator.class.getClassLoader().getResourceAsStream(cacheConfig);
                }

                ClassLoader cacheClassLoader = DefaultCacheManager.class.getClassLoader();
                Thread.currentThread().setContextClassLoader(cacheClassLoader);
                ConfigurationBuilderHolder holder = new ParserRegistry(cacheClassLoader).parse(configStream);
                if (jgroupsConfig != null) {
                    holder.getGlobalConfigurationBuilder()
                          .transport()
                          .defaultTransport()
                          .addProperty("configurationFile", jgroupsConfig);
                }
                _cache = new DefaultCacheManager(holder, true).getCache(cacheName);
            } finally {
                if (configStream != null) {
                    try {
                        configStream.close();
                    } catch (Exception e) {
                        e.fillInStackTrace();
                    }
                }
           }
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
