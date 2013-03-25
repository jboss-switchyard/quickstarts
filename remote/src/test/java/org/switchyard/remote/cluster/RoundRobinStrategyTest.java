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
