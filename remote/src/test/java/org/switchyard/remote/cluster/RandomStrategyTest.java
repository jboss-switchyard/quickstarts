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
package org.switchyard.remote.cluster;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.remote.RemoteEndpoint;
import org.switchyard.remote.RemoteRegistry;

public class RandomStrategyTest {
    
    private static final QName TEST_SERVICE = new QName("RandomStrategy");
    private RemoteRegistry registry = new MockRegistry();
    private RandomStrategy random = new RandomStrategy();
    
    @Before
    public void setUp() throws Exception {
        random.setRegistry(registry);
    }

    @Test
    public void noEndpoints() {
        Assert.assertNull(random.selectEndpoint(TEST_SERVICE));
    }
    
    @Test
    public void oneEndpoint() {
        registry.addEndpoint(new RemoteEndpoint().setServiceName(TEST_SERVICE));
        Assert.assertNotNull(random.selectEndpoint(TEST_SERVICE));
    }
    
    @Test
    public void multipleEndpoints() {
        // register two endpoints for the same service
        RemoteEndpoint ep1 = new RemoteEndpoint().setServiceName(TEST_SERVICE).setEndpoint("ep1");
        RemoteEndpoint ep2 = new RemoteEndpoint().setServiceName(TEST_SERVICE).setEndpoint("ep2");
        registry.addEndpoint(ep1);
        registry.addEndpoint(ep2);
        
        Map<String, AtomicInteger> epCounts = new HashMap<String, AtomicInteger>();
        epCounts.put(ep1.getEndpoint(), new AtomicInteger());
        epCounts.put(ep2.getEndpoint(), new AtomicInteger());
        for (int i = 0; i < 1000; i++) {
            RemoteEndpoint ep = random.selectEndpoint(TEST_SERVICE);
            epCounts.get(ep.getEndpoint()).incrementAndGet();
        }
        
        Assert.assertTrue(epCounts.get(ep1.getEndpoint()).get() > 0);
        Assert.assertTrue(epCounts.get(ep2.getEndpoint()).get() > 0);
    }
}
