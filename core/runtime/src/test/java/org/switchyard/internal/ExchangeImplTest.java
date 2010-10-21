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

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Context;
import org.switchyard.Direction;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.Message;
import org.switchyard.MessageBuilder;
import org.switchyard.Scope;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.event.ExchangeInEvent;
import org.switchyard.event.ExchangeOutEvent;
import org.switchyard.internal.ExchangeImpl;
import org.switchyard.internal.ServiceDomains;

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
	
	/** Setting a property in one scope should not bleed over into other scopes.
	 *
	 */
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
	
	/*
	 * The message scope changes each time an exchange is sent.  Test to
	 * make sure that actually happens (e.g. you don't get the "in" scope
	 * all the time.
	 */
	@Test
	public void testMessageContext() throws Exception {
		
		final QName serviceName = new QName("bleh");
		final String sharedPropName = "both";  	// should be in both msgs
		final String inPropName= "in";      	// should only be in one
		final String inPropVal = "in";
		final String outPropVal = "out";
		
		// create a handler to test that the in and out context are separate
		ExchangeHandler handler = new BaseHandler(Direction.RECEIVE) {
			public void exchangeIn(ExchangeInEvent event) {
				// We should find the shared property with the in value
				Assert.assertEquals(
						event.getExchange().getContext(Scope.MESSAGE).getProperty(sharedPropName), 
						inPropVal);
				// We should find the in property with the in value
				Assert.assertEquals(
						event.getExchange().getContext(Scope.MESSAGE).getProperty(inPropName), 
						inPropVal);
				
				try {
					Context outCtx = event.getExchange().createContext();
					outCtx.setProperty(sharedPropName, outPropVal);
					event.getExchange().sendOut(MessageBuilder.newInstance().buildMessage(), outCtx);
				}
				catch (Exception ex) {
					Assert.fail(ex.toString());
				}
			}
			
			public void exchangeOut(ExchangeOutEvent event) {
				// We should find the shared property with the out value
				Assert.assertEquals(
						event.getExchange().getContext(Scope.MESSAGE).getProperty(sharedPropName), 
						outPropVal);
				// The in property should not be there
				Assert.assertNull(
						event.getExchange().getContext(Scope.MESSAGE).getProperty(inPropName));
			}
		};
		
		Service service = _domain.registerService(serviceName, handler);
		Exchange exchange = _domain.createExchange(
				serviceName, ExchangePattern.IN_OUT, handler);
		Message inMsg = MessageBuilder.newInstance().buildMessage();
		Context inCtx = exchange.createContext();
		inCtx.setProperty(sharedPropName, inPropVal);
		inCtx.setProperty(inPropName, inPropVal);
		exchange.sendIn(inMsg, inCtx);
		
		// clean up
		service.unregister();
		
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
		
		Service service = _domain.registerService(serviceName, handler);
		Exchange exchange = _domain.createExchange(
				serviceName, ExchangePattern.IN_OUT, handler);
		Message inMsg = MessageBuilder.newInstance().buildMessage();
		inMsg.setContent(inMsgContent);
		exchange.sendIn(inMsg);

		// clean up
		service.unregister();
	}
}
