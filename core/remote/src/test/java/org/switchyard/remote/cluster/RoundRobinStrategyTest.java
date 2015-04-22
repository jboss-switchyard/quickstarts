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

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.remote.RemoteEndpoint;
import org.switchyard.remote.RemoteRegistry;

public class RoundRobinStrategyTest {
    
    private static final QName TEST_SERVICE1 = new QName("RoundRobinStrategy1");
    private static final QName TEST_SERVICE2 = new QName("RoundRobinStrategy2");
    
    private RemoteRegistry registry = new MockRegistry();
    private RoundRobinStrategy robin = new RoundRobinStrategy();

    @Before
    public void setUp() throws Exception {
        robin.setRegistry(registry);
    }
    
    @Test
    public void noEndpoints() {
        Assert.assertNull(robin.selectEndpoint(TEST_SERVICE1));
    }
    
    @Test
    public void oneEndpoint() {
        registry.addEndpoint(new RemoteEndpoint().setServiceName(TEST_SERVICE1));
        Assert.assertNotNull(robin.selectEndpoint(TEST_SERVICE1));
    }
    
    @Test
    public void multipleEndpoints() {
        // register endpoints for each service
        RemoteEndpoint ep1 = new RemoteEndpoint().setServiceName(TEST_SERVICE1).setEndpoint("ep1");
        RemoteEndpoint ep2 = new RemoteEndpoint().setServiceName(TEST_SERVICE1).setEndpoint("ep2");
        RemoteEndpoint ep3 = new RemoteEndpoint().setServiceName(TEST_SERVICE2).setEndpoint("ep3");
        RemoteEndpoint ep4 = new RemoteEndpoint().setServiceName(TEST_SERVICE2).setEndpoint("ep4");
        registry.addEndpoint(ep1);
        registry.addEndpoint(ep2);
        registry.addEndpoint(ep3);
        registry.addEndpoint(ep4);
        
        // check to see we get round robin behavior
        Assert.assertEquals(ep1, robin.selectEndpoint(TEST_SERVICE1));
        Assert.assertEquals(ep3, robin.selectEndpoint(TEST_SERVICE2));
        Assert.assertEquals(ep2, robin.selectEndpoint(TEST_SERVICE1));
        Assert.assertEquals(ep4, robin.selectEndpoint(TEST_SERVICE2));
        Assert.assertEquals(ep1, robin.selectEndpoint(TEST_SERVICE1));
    }
}
