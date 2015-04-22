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
