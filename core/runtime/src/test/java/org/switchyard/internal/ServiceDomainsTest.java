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

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.ServiceDomain;
import org.switchyard.internal.ServiceDomains;

/**
 *  Unit tests for the ServiceDomains class.
 */
public class ServiceDomainsTest {
	
	@Test
	public void testGetDomain() {
		// Should return the default domain
		ServiceDomain domain = ServiceDomains.getDomain();
		Assert.assertEquals(domain.getName(), ServiceDomains.ROOT_DOMAIN);
		
		// Multiple calls should return the same domain
		ServiceDomain domain2 = ServiceDomains.getDomain();
		Assert.assertEquals(domain, domain2);
	}
	
	@Test
	public void testCreateDomain() throws Exception {
		final String DOMAIN1 = "foo";
		final String DOMAIN2 = "bar";
		ServiceDomain domain = ServiceDomains.createDomain(DOMAIN1);
		Assert.assertEquals(domain.getName(), DOMAIN1);
		
		// Multiple calls should return the same domain
		ServiceDomain domain2 = ServiceDomains.createDomain(DOMAIN2);
		Assert.assertNotSame(domain, domain2);
	}
	
}
