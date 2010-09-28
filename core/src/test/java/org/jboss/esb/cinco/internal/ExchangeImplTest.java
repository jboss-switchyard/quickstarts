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

import junit.framework.Assert;

import org.jboss.esb.cinco.Context;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.Scope;
import org.junit.Before;
import org.junit.Test;

/**
 *  Unit tests for the ExchangeImpl class.
 */
public class ExchangeImplTest {
	
	private ExchangeImpl _exchange;
	
	@Before
	public void setUp() throws Exception {
		_exchange = new ExchangeImpl(ExchangePattern.IN_ONLY);
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
		Context inMsgContext = _exchange.getContext(Scope.IN);
		
		exchangeContext.setProperty("exchangeProp", "exchangeVal");
		inMsgContext.setProperty("inMsgProp", "inMsgVal");
		
		Assert.assertTrue(exchangeContext.hasProperty("exchangeProp"));
		Assert.assertFalse(exchangeContext.hasProperty("inMsgProp"));
		
		Assert.assertTrue(inMsgContext.hasProperty("inMsgProp"));
		Assert.assertFalse(inMsgContext.hasProperty("exchangeProp"));
	}
}
