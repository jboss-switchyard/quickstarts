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

package org.jboss.esb.cinco.internal;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.jboss.esb.cinco.BaseHandler;
import org.jboss.esb.cinco.Context;
import org.jboss.esb.cinco.Direction;
import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeHandler;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.Message;
import org.jboss.esb.cinco.MessageBuilder;
import org.jboss.esb.cinco.Scope;
import org.jboss.esb.cinco.ServiceDomain;
import org.jboss.esb.cinco.event.ExchangeInEvent;
import org.jboss.esb.cinco.event.ExchangeOutEvent;
import org.junit.Before;
import org.junit.Test;

/**
 *  Unit tests for the ExchangeImpl class.
 */
public class ExchangeImplTest {
	
	private ExchangeImpl _exchange;
	private ServiceDomain _domain;
	
	@Before
	public void setUp() throws Exception {
		_exchange = new ExchangeImpl(new QName("bleh"), ExchangePattern.IN_ONLY, null);
		_domain = ServiceDomains.getDomain();
	}
	
	@Test
	public void testGetContextDefault() {
		Context defaultContext = _exchange.getContext();
		Context exchangeContext = _exchange.getContext(Scope.EXCHANGE);
		Assert.assertEquals(exchangeContext, defaultContext);
	}
	
	@Test
	public void testContextIsolation() {
		Context exchangeContext = _exchange.getContext(Scope.EXCHANGE);
		Context inMsgContext = _exchange.getContext(Scope.MESSAGE);
		
		exchangeContext.setProperty("exchangeProp", "exchangeVal");
		inMsgContext.setProperty("inMsgProp", "inMsgVal");
		
		Assert.assertTrue(exchangeContext.hasProperty("exchangeProp"));
		Assert.assertFalse(exchangeContext.hasProperty("inMsgProp"));
		
		Assert.assertTrue(inMsgContext.hasProperty("inMsgProp"));
		Assert.assertFalse(inMsgContext.hasProperty("exchangeProp"));
	}
	
	/**
	 * Make sure that the current message is set correctly when an exchange
	 * is sent.
	 */
	@Test
	public void testGetMessage() throws Exception {
		
		final QName serviceName = new QName("bleh");
		final String inMsgContent = "in message";
		final String outMsgContent = "out message";
		
		// create a handler to test that the in and out content match
		// expected result from getMessage()
		ExchangeHandler handler = new BaseHandler(Direction.RECEIVE) {
			public void exchangeIn(ExchangeInEvent event) {
				Assert.assertEquals(
						event.getExchange().getMessage().getContent(), 
						inMsgContent);
				
				Message outMsg = MessageBuilder.newInstance().buildMessage();
				outMsg.setContent(outMsgContent);
				try {
					event.getExchange().sendOut(outMsg);
				}
				catch (Exception ex) {
					Assert.fail(ex.toString());
				}
			}
			
			public void exchangeOut(ExchangeOutEvent event) {
				Assert.assertEquals(
						event.getExchange().getMessage().getContent(), 
						outMsgContent);
			}
		};
		
		_domain.registerService(serviceName, handler);
		Exchange exchange = _domain.createExchange(
				serviceName, ExchangePattern.IN_OUT, handler);
		Message inMsg = MessageBuilder.newInstance().buildMessage();
		inMsg.setContent(inMsgContent);
		exchange.sendIn(inMsg);
		
	}
}
