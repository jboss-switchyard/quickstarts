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
import org.infinispan.remoting.transport.Address;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.remote.RemoteEndpoint;

public class InfinispanRegistryTest {

    private InfinispanRegistry _registry;
    private DefaultCacheManager _cacheMgr;
    
    @Before
    public void setUp() {
        _cacheMgr = new DefaultCacheManager(new GlobalConfigurationBuilder()
            .transport().defaultTransport().build());
        
        _cacheMgr.defineConfiguration("test-cache", 
                new ConfigurationBuilder().invocationBatching().enable().build());
        Cache<String, String> cache = _cacheMgr.getCache("test-cache");
        _registry = new InfinispanRegistry(cache);
    }
    
    @After
    public void tearDown() {
        _cacheMgr.stop();
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
    public void testNodeFailure() throws Exception {
        RemoteEndpoint ep1 = new RemoteEndpoint()
        .setDomainName(new QName("domain1"))
        .setServiceName(new QName("service1"));
        RemoteEndpoint ep2 = new RemoteEndpoint()
        .setDomainName(new QName("domain1"))
        .setServiceName(new QName("service2"));
        
        // nothing in the registry
        Assert.assertEquals(0, _registry.getEndpoints(ep1.getServiceName()).size());
        // add our endpoints on the same node
        _registry.addEndpoint(ep1);
        _registry.addEndpoint(ep2);
        // signal that the node has failed
        InfinispanRegistry.MemberDropListener dropListener = _registry.new MemberDropListener();
        dropListener.dropAllServices(new FakeAddress(ep1.getNode()));

        // check to make sure all endpoints were removed
        Assert.assertEquals(0, _registry.getEndpoints(ep1.getServiceName()).size());
        // check to make sure all endpoints were removed
        Assert.assertEquals(0, _registry.getEndpoints(ep2.getServiceName()).size());
        
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

class FakeAddress implements Address {
    
    private String _address;
    
    public FakeAddress(String address) {
        _address = address;
    }
    
    @Override
    public String toString() {
        return _address;
    }

    @Override
    public int compareTo(Address address) {
        if (address == null) {
            return -1;
        }
        return _address.compareTo(address.toString());
    }
}
