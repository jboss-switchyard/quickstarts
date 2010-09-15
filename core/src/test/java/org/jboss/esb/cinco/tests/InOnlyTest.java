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

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.BaseHandler;
import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.ExchangeEvent;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.ServiceDomain;
import org.jboss.esb.cinco.event.ExchangeInEvent;
import org.jboss.esb.cinco.internal.ServiceDomains;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InOnlyTest {
	
	// where the magic happens
	private ServiceDomain _domain;
	// event counters used by tests
	private List<ExchangeEvent> inEvents = new LinkedList<ExchangeEvent>();

	@Before
	public void setUp() throws Exception {
		_domain = ServiceDomains.getDomain();
		_domain.start();
	}
	
	@After
	public void tearDown() throws Exception {
		inEvents.clear();
		_domain.stop();
	}
	
	@Test
	public void testInOnlySuccess() throws Exception {
		final QName serviceName = new QName("inOnlySuccess");
		// Provide the service
		ExchangeChannel providerChannel = _domain.createChannel();
		providerChannel.getHandlerChain().addFirst("provider", 
				new BaseHandler() {
					public void exchangeIn(ExchangeInEvent event) {
						inEvents.add(event);
					}
		});
		_domain.registerService(serviceName, providerChannel);
		
		// Consume the service
		ExchangeChannel consumerChannel = _domain.createChannel();
		Exchange exchange = consumerChannel.createExchange(ExchangePattern.IN_ONLY);
		exchange.setService(serviceName);
		consumerChannel.send(exchange);
		
		// wait a sec, since this is async
		Thread.sleep(200);
		Assert.assertTrue(inEvents.size() == 1);
	}

	@Test
	public void testInOnlyError() throws Exception {
		// Test a handler throwing an error here!
	}

}
