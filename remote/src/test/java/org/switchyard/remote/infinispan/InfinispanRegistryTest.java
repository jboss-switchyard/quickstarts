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

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.remote.RemoteEndpoint;

public class InfinispanRegistryTest {

    private InfinispanRegistry _registry;
    
    @Before
    public void setUp() {
        DefaultCacheManager cacheMgr = new DefaultCacheManager(new GlobalConfigurationBuilder()
            .transport().defaultTransport().build());
        
        cacheMgr.defineConfiguration("test-cache", 
                new ConfigurationBuilder().invocationBatching().enable().build());
        Cache<String, String> cache = cacheMgr.getCache("test-cache");
        _registry = new InfinispanRegistry(cache);
    }
    
    @Test
    public void addGetAndRemoveSingle() throws Exception {
        RemoteEndpoint ep1 = new RemoteEndpoint()
            .setDomainName(new QName("domain1"))
            .setServiceName(new QName("service1"));

        // nothing in the registry
        Assert.assertEquals(0, _registry.getEndpoints(ep1.getServiceName()).size());
        // add an endpoint
        _registry.addEndpoint(ep1);
        // should get one now
        Assert.assertEquals(1, _registry.getEndpoints(ep1.getServiceName()).size());
        // remove the endpoint
        _registry.removeEndpoint(ep1);
        // nothing in the registry
        Assert.assertEquals(0, _registry.getEndpoints(ep1.getServiceName()).size());
    }
    
    @Test
    public void addDuplicate() throws Exception {
        RemoteEndpoint ep1 = new RemoteEndpoint()
            .setDomainName(new QName("domain1"))
            .setServiceName(new QName("service1"));

        // add an endpoint
        _registry.addEndpoint(ep1);
        // should get one now
        Assert.assertEquals(1, _registry.getEndpoints(ep1.getServiceName()).size());
        // attempt to add again
        _registry.addEndpoint(ep1);
        // still just one in the registry
        Assert.assertEquals(1, _registry.getEndpoints(ep1.getServiceName()).size());
    }
}
