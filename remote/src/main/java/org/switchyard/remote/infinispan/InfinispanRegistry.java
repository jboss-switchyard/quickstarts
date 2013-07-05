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
package org.switchyard.remote.infinispan;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;
import org.infinispan.remoting.transport.Address;
import org.switchyard.remote.RemoteEndpoint;
import org.switchyard.remote.RemoteRegistry;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;

/**
 * Implementation of a distributed registry based on a replicated cache in Infinispan.
 * This class provides an implementation of the RemoteRegistry contract as well as a group
 * membership listener to detect node failures and remove endpoint registrations from that node.
 */
public class InfinispanRegistry implements RemoteRegistry {

    private static final QName ROOT_DOMAIN = new QName("all-domains");
    
    private static Logger _log = Logger.getLogger(InfinispanRegistry.class);

    private String _nodeName;
    private Cache<String, String> _serviceCache;
    private Serializer _serializer;
    
    /**
     * Create a new InfinispanRegistry using the specified replicated cache.
     * @param serviceCache the replicated cache to use
     */
    public InfinispanRegistry(Cache<String, String> serviceCache) {
        _serviceCache = serviceCache;
        _serializer = SerializerFactory.create(FormatType.JSON, null, true);
    
        serviceCache.getCacheManager().addListener(new MemberDropListener());
        _nodeName = serviceCache.getCacheManager().getAddress().toString();
    }
    
    @Override
    public void addEndpoint(RemoteEndpoint endpoint) {
        String cacheKey = createNodeKey(ROOT_DOMAIN, endpoint.getServiceName(), _nodeName);
        if (_serviceCache.get(cacheKey) != null) {
            _log.info("Remote endpoint " + cacheKey + " is already registered in the cache");
            return;
        }
        
        try {
            endpoint.setNode(_nodeName);
            String epStr = new String(_serializer.serialize(endpoint, RemoteEndpoint.class));
            _serviceCache.put(cacheKey, epStr);
        } catch (java.io.IOException ioEx) {
            _log.warn("Failed to add remote endpoint " + cacheKey + " to registry.", ioEx);
        }
    }

    @Override
    public void removeEndpoint(RemoteEndpoint endpoint) {
        if (_nodeName != null) {
            _serviceCache.remove(createNodeKey(ROOT_DOMAIN, endpoint.getServiceName(), _nodeName));
        }
    }

    @Override
    public List<RemoteEndpoint> getEndpoints(QName serviceName) {
        List<RemoteEndpoint> services = new LinkedList<RemoteEndpoint>();
        // add remotes and prune the local entry
        String serviceKey = createServiceKey(ROOT_DOMAIN, serviceName);
        Set<String> nodes = _serviceCache.keySet();
        if (nodes != null) {
            for (String node : nodes) {
                if (node.startsWith(serviceKey)) {
                    String epStr = _serviceCache.get(node);
                    // Catch a race condition where entry has been removed since keySet list was built
                    if (epStr != null) {
                        try {
                            RemoteEndpoint ep = _serializer.deserialize(epStr.getBytes(), RemoteEndpoint.class);
                            services.add(ep);
                        } catch (java.io.IOException ioEx) {
                            _log.warn("Failed to deserialize remote endpoint: " + epStr, ioEx);
                        }
                    }
                }
            }
        }
        return services;
    }
    
    private String createServiceKey(QName domain, QName service) {
        return "/" + domain.toString() + "/" + service.toString();
    }
    
    private String createNodeKey(QName domain, QName service, String node) {
        return createServiceKey(domain, service) + "/" + node;
    }
    
    /**
     * Listeners for members which get dropped and removes any endpoint registrations for 
     * dropped nodes in the registry.
     */
    @Listener
    public class MemberDropListener {
        /**
         * Triggered when a view is changed, signaling that a member has joined or dropped from the cluster.
         * @param event change details
         */
        @ViewChanged
        public void viewChanged(ViewChangedEvent event) {
            List<Address> dropped = new ArrayList<Address>(event.getOldMembers());
            dropped.removeAll(event.getNewMembers());
            for (Address addr : dropped) {
                dropAllServices(addr);
            }
        }
        
        void dropAllServices(Address address) {
            for (String node : _serviceCache.keySet()) {
                if (node.endsWith("/" + address.toString())) {
                    _serviceCache.remove(node);
                }
            }
        }
    }
}
