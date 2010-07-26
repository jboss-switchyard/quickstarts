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

package org.jboss.esb.cinco.tests;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.internal.Environment;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InOnlyTest {
	
	private final QName SERVICE_1 = new QName("service1");
	private Environment _env;

	@Before
	public void setUp() throws Exception {
		_env = new Environment();
	}
	
	@After
	public void tearDown() throws Exception {
		_env.destroy();
	}
	
	@Test
	public void testOneWaySingle() throws Exception {
		// Create a consumer instance
		OneWayConsumer consumer = new OneWayConsumer(
				_env.getExchangeFactory(), 
				_env.getExchangeChannelFactory().createChannel());
		
		// Create a provider instance
		OneWayProvider provider = new OneWayProvider(
				_env.getExchangeChannelFactory().createChannel());
		
		provider.provideService(SERVICE_1);
		consumer.invokeService(SERVICE_1, null);
		
		// wait a sec, since this is async
		Thread.sleep(200);
		
		Assert.assertTrue(provider.getReceiveCount() == consumer.getSendCount());
		Assert.assertTrue(consumer.getActiveCount() == 0);
	}
	
}
