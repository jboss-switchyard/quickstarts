/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.internal;

import static org.hamcrest.CoreMatchers.is;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.Service;
import org.switchyard.internal.DefaultServiceRegistry;

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
        Service service = registry.registerService(serviceName, null, null, null);
        Assert.assertTrue(registry.getServices(serviceName).size() > 0);
        service.unregister();
        Assert.assertTrue(registry.getServices(serviceName).size() == 0);
    }

}
