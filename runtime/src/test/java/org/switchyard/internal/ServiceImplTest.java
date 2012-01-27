/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.internal;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.Service;
import org.switchyard.metadata.InOnlyService;

/**
 *  Unit tests for the ServiceImpl class.
 */
public class ServiceImplTest {
     
    private MockDomain _domain;
    
    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
    }
    
    @Test
    public void testUnregister() {
        Service service = _domain.registerService(new QName("TestService"), 
                new InOnlyService(), new MockHandler());
        // test that it was added to the registry
        Assert.assertEquals(1, _domain.getServiceRegistry().getServices().size());
        service.unregister();
        // confirm that it was removed
        Assert.assertEquals(0, _domain.getServiceRegistry().getServices().size());
    }
    
}
