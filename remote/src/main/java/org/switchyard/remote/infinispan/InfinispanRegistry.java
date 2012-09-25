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
import org.infinispan.tree.Node;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.switchyard.remote.RemoteEndpoint;
import org.switchyard.remote.RemoteRegistry;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;

/**
 * Implementation of a distributed registry based on a replicated tree cache in Infinispan.
 * This class provides an implementation of the RemoteRegistry contract as well as a group
 * membership listener to detect node failures and remove endpoint registrations from that node.
 */
public class InfinispanRegistry implements RemoteRegistry {

    private static final QName ROOT_DOMAIN = new QName("all-domains");
    
    private static Logger _log = Logger.getLogger(InfinispanRegistry.class);

    private String _nodeName;
    private TreeCache<String, String> _serviceCache;
    private Serializer _serializer = SerializerFactory.create(FormatType.JSON, null, true);
    
    /**
     * Create a new InfinispanRegistry using the specified replicated cache.
     * @param serviceCache the replicated cache to use
     */
    public InfinispanRegistry(Cache<String, String> serviceCache) {
        _serviceCache = new TreeCacheFactory().createTreeCache(serviceCache);
        _serializer = SerializerFactory.create(FormatType.JSON, null, true);
    
        serviceCache.getCacheManager().addListener(new MemberDropListener());
        _nodeName = serviceCache.getCacheManager().getAddress().toString();
    }
    
    @Override
    public void addEndpoint(RemoteEndpoint endpoint) {
        if (_serviceCache.get(toFQN(ROOT_DOMAIN, endpoint.getServiceName()), _nodeName) != null) {
            _log.info("Service " + endpoint.getServiceName() + " is already registered in the cache");
            return;
        }
        
        try {
            endpoint.setNode(_nodeName);
            String epStr = new String(_serializer.serialize(endpoint, RemoteEndpoint.class));
            _serviceCache.put(toFQN(ROOT_DOMAIN, endpoint.getServiceName()), _nodeName, epStr);
        } catch (java.io.IOException ioEx) {
            _log.warn("Failed to add service " + endpoint.getServiceName() + " to remote registry.", ioEx);
        }
    }

    @Override
    public void removeEndpoint(RemoteEndpoint endpoint) {
        if (_nodeName != null) {
            _serviceCache.remove(toFQN(ROOT_DOMAIN, endpoint.getServiceName()), _nodeName);
        }
    }

    @Override
    public List<RemoteEndpoint> getEndpoints(QName serviceName) {
        List<RemoteEndpoint> services = new LinkedList<RemoteEndpoint>();
        // add remotes and prune the local entry
        String serviceFQN = toFQN(ROOT_DOMAIN, serviceName);
        Set<String> nodes = _serviceCache.getKeys(serviceFQN);
        if (nodes != null) {
            for (String node : nodes) {
                String epStr = _serviceCache.get(serviceFQN, node);
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
        return services;
    }
    
    private String toFQN(QName domain) {
        return "/" + domain.toString();
    }
    
    private String toFQN(QName domain, QName service) {
        return toFQN(domain) + "/" + service.toString();
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
        
        private void dropAllServices(Address address) {
            for (Node<String, String> domainNode : _serviceCache.getRoot().getChildren()) {
                for (Node<String, String> serviceNode : domainNode.getChildren()) {
                    serviceNode.remove(address.toString());
                }
            }
        }
    }
}
