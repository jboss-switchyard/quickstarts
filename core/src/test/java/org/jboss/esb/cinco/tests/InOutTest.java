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

import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeFactory;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.MessageFactory;
import org.jboss.esb.cinco.internal.Environment;
import org.jboss.esb.cinco.internal.ExchangeState;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InOutTest {
	
	private final QName SERVICE_1 = new QName("InOutTest");
	private Environment _env;
	private BaseConsumer _consumer;
	private BaseProvider _provider;
	private ExchangeFactory _exchangeFactory;
	private MessageFactory _messageFactory;

	@Before
	public void setUp() throws Exception {
		_env = new Environment();
		_exchangeFactory = _env.getExchangeFactory();
		_messageFactory = _env.getMessageFactory();

		// Create a consumer instance
		_consumer = new BaseConsumer(_env);
		// Create a provider instance
		_provider = new BaseProvider(_env, ExchangeState.OUT);
		_provider.setReply(_messageFactory.createMessage());
	}
	
	@After
	public void tearDown() throws Exception {
		_env.destroy();
	}
	
	@Test
	public void testInOutSuccess() throws Exception {
		
		Exchange inOnly = _exchangeFactory.createExchange(
				ExchangePattern.IN_OUT);
		_provider.provideService(SERVICE_1);
		_consumer.invokeService(inOnly, SERVICE_1, null);
		
		// wait a sec, since this is async
		Thread.sleep(200);
		
		Assert.assertTrue(_provider.getReceiveCount() == _consumer.getSendCount());
		Assert.assertTrue(_consumer.getOutCount() == 1);
	}

	@Test
	public void testInOutFault() throws Exception {

		// set our provider to return a fault
		_provider.setNextState(ExchangeState.FAULT);
		
		Exchange inOnly = _exchangeFactory.createExchange(
				ExchangePattern.IN_OUT);
		_provider.provideService(SERVICE_1);
		_consumer.invokeService(inOnly, SERVICE_1, null);
		
		// wait a sec, since this is async
		Thread.sleep(200);
		
		Assert.assertTrue(_provider.getReceiveCount() == _consumer.getSendCount());
		Assert.assertTrue(_consumer.getFaultCount() == 1);
	}

	@Test
	public void testInOutError() throws Exception {
		
		// set our provider to return an error
		_provider.setNextState(ExchangeState.ERROR);
		
		Exchange inOnly = _exchangeFactory.createExchange(
				ExchangePattern.IN_OUT);
		_provider.provideService(SERVICE_1);
		_consumer.invokeService(inOnly, SERVICE_1, null);
		
		// wait a sec, since this is async
		Thread.sleep(200);
		
		Assert.assertTrue(_provider.getReceiveCount() == _consumer.getSendCount());
		Assert.assertTrue(_consumer.getErrorCount() == 1);
	}

}
