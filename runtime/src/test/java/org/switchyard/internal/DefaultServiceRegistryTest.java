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

import static org.hamcrest.CoreMatchers.is;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.Service;

/**
 * Unit test for {@link DefaultServiceRegistry}
 * </p>
 * @author Daniel Bevenius
 *
 */
public class DefaultServiceRegistryTest
{   
    @Test
    public void shouldBePossibleToSearchForNonRegisteredService()
    {
        DefaultServiceRegistry registry = new DefaultServiceRegistry();
        List<Service> services = registry.getServices(new QName("unRegisteredService"));
        Assert.assertThat(services.size(), is(0));
    }
    
    @Test
    public void testUnregister() {
        final QName serviceName = new QName("Foo");
        DefaultServiceRegistry registry = new DefaultServiceRegistry();
        ServiceImpl service = new ServiceImpl(serviceName, null, null, null);
        registry.registerService(service);
        Assert.assertTrue(registry.getServices(serviceName).size() > 0);
        registry.unregisterService(service);
        Assert.assertTrue(registry.getServices(serviceName).size() == 0);
    }

    @Test
    public void referenceRegistration() {
        final QName referenceName = new QName("Bar");
        DefaultServiceRegistry registry = new DefaultServiceRegistry();
        ServiceReferenceImpl reference = new ServiceReferenceImpl(referenceName, null, null, null);
        registry.registerServiceReference(reference);
        Assert.assertTrue(registry.getServiceReferences().size() > 0);
        registry.unregisterServiceReference(reference);
        Assert.assertTrue(registry.getServices(referenceName).size() == 0);
    }
}
