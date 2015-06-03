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
