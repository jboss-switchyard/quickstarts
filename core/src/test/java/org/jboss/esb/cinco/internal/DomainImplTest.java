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

import org.jboss.esb.cinco.Context;
import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.Scope;
import org.junit.Before;
import org.junit.Test;

/**
 *  Unit tests for the DomainImpl class.
 */
public class DomainImplTest {
	 
	private static final QName SERVICE = new QName("Service");
	private DomainImpl _domain;
	
	@Before
	public void setUp() throws Exception {
		_domain = new DomainImpl();
	}
	
	@Test
	public void testCreateExchange() {
		Exchange inOnly = _domain.createExchange(SERVICE, ExchangePattern.IN_ONLY);
		Assert.assertEquals(ExchangePattern.IN_ONLY, inOnly.getPattern());
		Exchange inOut = _domain.createExchange(SERVICE, ExchangePattern.IN_OUT);
		Assert.assertEquals(ExchangePattern.IN_OUT, inOut.getPattern());
	}
	
	/** Disabled for the moment
	@Test
	public void testCreateExchangeCorrelation() {
		final String correlationId = "foo123";
		// create the relatesTo exchange
		Exchange ex1 = _domain.createExchange(SERVICE, ExchangePattern.IN_ONLY);
		ex1.getContext(Scope.CORRELATION).setProperty(Context.CORRELATION_ID, correlationId);
		ex1.getContext(Scope.MESSAGE).setProperty(Context.MESSAGE_ID, "bar789");
		
		// create a new exchange and use ex1 as relatesTo
		Exchange ex2 = _domain.createExchange(SERVICE, ExchangePattern.IN_ONLY, ex1);
		// make sure correlation context is propagated and nothing else
		Assert.assertEquals(correlationId, 
				ex2.getContext(Scope.CORRELATION).getProperty(Context.CORRELATION_ID));
		Assert.assertFalse(ex2.getContext(Scope.IN).hasProperty(Context.MESSAGE_ID));
	}
	*/
}
