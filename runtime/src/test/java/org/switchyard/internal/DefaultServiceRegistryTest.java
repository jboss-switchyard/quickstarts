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
        registry.registerService(service, null, null);
        Assert.assertTrue(registry.getServices(serviceName).size() > 0);
        registry.unregisterService(service);
        Assert.assertTrue(registry.getServices(serviceName).size() == 0);
    }

}
