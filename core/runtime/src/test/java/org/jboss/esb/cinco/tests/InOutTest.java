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

import junit.framework.Assert;

import org.jboss.esb.cinco.BaseHandler;
import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeEvent;
import org.jboss.esb.cinco.ExchangeHandler;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.MessageBuilder;
import org.jboss.esb.cinco.ServiceDomain;
import org.jboss.esb.cinco.event.ExchangeFaultEvent;
import org.jboss.esb.cinco.event.ExchangeInEvent;
import org.jboss.esb.cinco.event.ExchangeOutEvent;
import org.jboss.esb.cinco.internal.ServiceDomains;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InOutTest {
	
	// where the magic happens
	private ServiceDomain _domain;
	// event counters used by tests
	private List<ExchangeEvent> inEvents = new LinkedList<ExchangeEvent>();
	private List<ExchangeEvent> outEvents = new LinkedList<ExchangeEvent>();
	private List<ExchangeEvent> faultEvents = new LinkedList<ExchangeEvent>();

	@Before
	public void setUp() throws Exception {
		_domain = ServiceDomains.getDomain();
	}
	
	@After
	public void tearDown() throws Exception {
		inEvents.clear();
		outEvents.clear();
		faultEvents.clear();
	}
	

	/** NEW WAY **/
	@Test
	public void testInOutSuccess() throws Exception {
		final QName serviceName = new QName("inOutSuccess");
		// Provide the service
		ExchangeHandler provider = 
				new BaseHandler() {
					public void exchangeIn(ExchangeInEvent event) {
						inEvents.add(event);
						Exchange inEx = event.getExchange();
						try {
							inEx.sendOut(MessageBuilder.newInstance().buildMessage());
						}
						catch (Exception ex) {
							Assert.fail(ex.toString());
						}
					}
		};
		_domain.registerService(serviceName, provider);
		
		// Consume the service
		ExchangeHandler consumer =
				new BaseHandler() {
					public void exchangeOut(ExchangeOutEvent event) {
						outEvents.add(event);
					}
		};
		Exchange exchange = _domain.createExchange(
				serviceName, ExchangePattern.IN_OUT, consumer);
		exchange.sendIn(MessageBuilder.newInstance().buildMessage());
		
		// wait a sec, since this is async
		Thread.sleep(200);
		Assert.assertTrue(inEvents.size() == 1);
		Assert.assertTrue(outEvents.size() == 1);
	}
	
	/** OLD WAY
	@Test
	public void testInOutSuccess() throws Exception {
		final QName serviceName = new QName("inOutSuccess");
		// Provide the service
		ExchangeChannel providerChannel = _domain.createChannel();
		providerChannel.getHandlerChain().addFirst("provider", 
				new BaseHandler() {
					public void exchangeIn(ExchangeInEvent event) {
						inEvents.add(event);
						Exchange inEx = event.getExchange();
						inEx.setOut(MessageBuilder.newInstance().buildMessage());
						event.getChannel().send(inEx);
					}
		});
		_domain.registerService(serviceName, providerChannel);
		
		// Consume the service
		ExchangeChannel consumerChannel = _domain.createChannel();
		consumerChannel.getHandlerChain().addFirst("consumer", 
				new BaseHandler() {
					public void exchangeOut(ExchangeOutEvent event) {
						outEvents.add(event);
					}
		});
		Exchange exchange = consumerChannel.createExchange(ExchangePattern.IN_OUT);
		exchange.setService(serviceName);
		consumerChannel.send(exchange);
		
		// wait a sec, since this is async
		Thread.sleep(200);
		Assert.assertTrue(inEvents.size() == 1);
		Assert.assertTrue(outEvents.size() == 1);
	}
	*/

	@Test
	public void testInOutFault() throws Exception {
		
		final QName serviceName = new QName("inOutFault");
		// Provide the service
		ExchangeHandler provider = 
				new BaseHandler() {
					public void exchangeIn(ExchangeInEvent event) {
						inEvents.add(event);
						Exchange inEx = event.getExchange();
						try {
							inEx.sendFault(MessageBuilder.newInstance().buildMessage());
						}
						catch (Exception ex) {
							Assert.fail(ex.toString());
						}
					}
		};
		_domain.registerService(serviceName, provider);
		
		// Consume the service
		ExchangeHandler consumer =
				new BaseHandler() {
					public void exchangeFault(ExchangeFaultEvent event) {
						faultEvents.add(event);
					}
		};
		Exchange exchange = _domain.createExchange(
				serviceName, ExchangePattern.IN_OUT, consumer);
		exchange.sendIn(MessageBuilder.newInstance().buildMessage());
		
		// wait a sec, since this is async
		Thread.sleep(200);
		Assert.assertTrue(inEvents.size() == 1);
		Assert.assertTrue(faultEvents.size() == 1);
		
	}
}
