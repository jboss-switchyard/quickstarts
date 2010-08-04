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

import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.MessageFactory;
import org.jboss.esb.cinco.internal.ExchangeState;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InOnlyTest {
	
	private final QName SERVICE_1 = new QName("InOnlyTest");
	private Environment _env;
	private BaseConsumer _consumer;
	private BaseProvider _provider;
	private MessageFactory _messageFactory;

	@Before
	public void setUp() throws Exception {
		_env = new Environment();
		_messageFactory = _env.getMessageFactory();

		// Create a consumer instance
		_consumer = new BaseConsumer(_env);
		// Create a provider instance
		_provider = new BaseProvider(
				_env.getExchangeChannelFactory().createChannel(), ExchangeState.DONE);
		_provider.setReply(_messageFactory.createMessage());
		
	}
	
	@After
	public void tearDown() throws Exception {
		_env.destroy();
	}
	
	@Test
	public void testInOnlySuccess() throws Exception {
		
		_provider.provideService(SERVICE_1);
		_consumer.invokeService(ExchangePattern.IN_ONLY, SERVICE_1, null);
		
		// wait a sec, since this is async
		Thread.sleep(200);
		
		Assert.assertTrue(_provider.getReceiveCount() == _consumer.getSendCount());
	}

	@Test
	public void testInOnlyError() throws Exception {
		
		// set our provider to return an error
		_provider.setNextState(ExchangeState.ERROR);
		
		_provider.provideService(SERVICE_1);
		_consumer.invokeService(ExchangePattern.IN_ONLY, SERVICE_1, null);
		
		// wait a sec, since this is async
		Thread.sleep(200);
		
		Assert.assertTrue(_provider.getReceiveCount() == _consumer.getSendCount());
		Assert.assertTrue(_consumer.getErrorCount() == 1);
	}

}
