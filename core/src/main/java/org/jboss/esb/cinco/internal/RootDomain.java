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

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.HandlerChain;
import org.jboss.esb.cinco.ServiceDomain;
import org.jboss.esb.cinco.internal.handlers.AddressingHandler;
import org.jboss.esb.cinco.internal.handlers.DeliveryHandler;
import org.jboss.esb.cinco.spi.ServiceRegistry;

public class RootDomain implements ServiceDomain {
	
	private HandlerChain _systemHandlers;
	private ServiceRegistry _registry;
	private Set<QName> _services = new HashSet<QName>();
	
	public RootDomain() {
		_registry = new DefaultServiceRegistry();
		
		// Build out the system handlers chain.  It would be cleaner if we 
		// handled this via config.
		_systemHandlers = new DefaultHandlerChain();
		_systemHandlers.addFirst("addressing", new AddressingHandler(_registry));
		_systemHandlers.addLast("delivery", new DeliveryHandler());
	}
	
	@Override
	public ExchangeChannel createChannel() {
		 ExchangeChannel channel = new ExchangeChannelImpl();
		// Add system handlers to channel
		channel.getHandlerChain().addLast("system handlers", _systemHandlers);
		return channel;
	}
	

	@Override
	public synchronized void close() {
		
	}
	

	@Override
	public void registerService(QName serviceName, ExchangeChannel channel) {
		_registry.registerService(serviceName, (ExchangeChannelImpl)channel);
		_services.add(serviceName);

	}
	
	@Override
	public void unregisterService(QName serviceName, ExchangeChannel channel) {
		_registry.unregisterService(serviceName, (ExchangeChannelImpl)channel);
		_services.remove(serviceName);

	}

}
