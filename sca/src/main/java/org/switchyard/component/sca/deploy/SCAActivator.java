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
package org.switchyard.component.sca.deploy;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.switchyard.component.sca.RemoteEndpointPublisher;
import org.switchyard.component.sca.SCAEndpoint;
import org.switchyard.component.sca.SCAInvoker;
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
    static final String[] TYPES = new String[] {"sca"};

    private static Logger _log = Logger.getLogger(RemoteRegistry.class);
    private RemoteRegistry _registry;
    private RemoteEndpointPublisher _endpointPublisher;
    
    /**
     * Create a new RemoteActivator.
     * @param environment component configuration used by the activator
     */
    public SCAActivator(Configuration environment) {
        super(TYPES);
        String cacheName = "cluster";
        
        try {
            // Attempt to resolve the cache container to use for the distributed registry
            Configuration cacheConfig = environment.getFirstChild(CACHE_NAME_PROPERTY);
            if (cacheConfig != null) {
                cacheName = cacheConfig.getValue();
            }
            // Now lookup the cache container in JNDI
            EmbeddedCacheManager cm = (EmbeddedCacheManager)
                    new InitialContext().lookup(CACHE_CONTAINER_ROOT + cacheName);
            Cache<String, String> cache = cm.getCache();
            
            // Configure a registry client to use the specified cache
            _registry = new InfinispanRegistry(cache);
        } catch (javax.naming.NamingException nEx) {
            _log.info("Unable to resolve cache-container " + cacheName 
                   +  ".  clustering for <binding.sca> will not be available for services and references.");
        }
    }
    
    @Override
    public ServiceHandler activateBinding(QName name, BindingModel config) {
        SCABindingModel scab = (SCABindingModel)config;
        if (scab.isServiceBinding()) {
            return new SCAEndpoint(scab, super.getServiceDomain(), _endpointPublisher, _registry);
        } else {
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
}
