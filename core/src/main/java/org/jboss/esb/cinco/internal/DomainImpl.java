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

import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeHandler;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.HandlerChain;
import org.jboss.esb.cinco.Service;
import org.jboss.esb.cinco.ServiceDomain;
import org.jboss.esb.cinco.internal.handlers.AddressingHandler;
import org.jboss.esb.cinco.internal.handlers.DeliveryHandler;
import org.jboss.esb.cinco.spi.Endpoint;
import org.jboss.esb.cinco.spi.EndpointProvider;
import org.jboss.esb.cinco.spi.ServiceRegistry;

public class DomainImpl implements ServiceDomain {
	
	private HandlerChain _systemHandlers;
	private ServiceRegistry _registry;
	private EndpointProvider _endpointProvider;

	public DomainImpl() {
		_registry = new DefaultServiceRegistry();
		_endpointProvider  = new DefaultEndpointProvider();
		
		// Build out the system handlers chain.  It would be cleaner if we 
		// handled this via config.
		_systemHandlers = new DefaultHandlerChain();
		_systemHandlers.addLast("addressing", new AddressingHandler(_registry));
		_systemHandlers.addLast("delivery", new DeliveryHandler());
	}
	

	@Override
	public Exchange createExchange(QName service, ExchangePattern pattern) {
		return createExchange(service, pattern, null);
	}



	@Override
	public Exchange createExchange(
			QName service, ExchangePattern pattern, ExchangeHandler handler) {
		// setup the system handlers
		HandlerChain handlers = new DefaultHandlerChain();
		handlers.addLast("system.handlers", _systemHandlers);
		// create the exchange
		ExchangeImpl exchange = new ExchangeImpl(service, pattern, handlers);
		
		if (handler != null) {
			// A response handler was specified, so setup a reply endpoint
			Endpoint ep = _endpointProvider.createEndpoint(handler);
			exchange.setSource(ep);
		}
		return exchange;
	}

	@Override
	public Service registerService(QName serviceName, ExchangeHandler handler) {
		Endpoint ep = _endpointProvider.createEndpoint(handler);
		HandlerChain handlers = new DefaultHandlerChain();
		handlers.addLast("provider", handler);
		return _registry.registerService(serviceName, ep, handlers, this);
	}
}
