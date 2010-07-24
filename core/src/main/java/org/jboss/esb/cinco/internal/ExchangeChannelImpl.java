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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.ExchangeEvent;
import org.jboss.esb.cinco.HandlerChain;
import org.jboss.esb.cinco.spi.ExchangeEndpoint;
import org.jboss.esb.cinco.spi.ServiceRegistry;

public class ExchangeChannelImpl 
	implements ExchangeChannel, ExchangeEndpoint, Runnable {
	
	private volatile boolean _isActive;
	private Thread _deliveryThread;
	private ServiceRegistry _registry;
	private HandlerChain 	_handlers = new DefaultHandlerChain();
	private Set<QName> 		_services = new HashSet<QName>();
	private BlockingQueue<Exchange> _exchanges = 
		new LinkedBlockingQueue<Exchange>();
	
	ExchangeChannelImpl(ServiceRegistry registry) {
		_registry = registry;
		startDelivery();
	}

	@Override
	public void close() {
		for (QName service : _services) {
			_registry.unregisterService(service, this);
		}
		
		stopDelivery();
	}

	@Override
	public HandlerChain getHandlerChain() {
		return _handlers;
	}

	@Override
	public void registerService(QName serviceName) {
		_registry.registerService(serviceName, this);
		_services.add(serviceName);

	}

	@Override
	public void send(Exchange exchange) {
		ExchangeEvent event = Events.createEvent(this, exchange);
		_handlers.handleSend(event);
	}

	@Override
	public void setHandlerChain(HandlerChain handlers) {
		_handlers = handlers;

	}

	@Override
	public void unregisterService(QName serviceName) {
		_registry.unregisterService(serviceName, this);
		_services.remove(serviceName);

	}

	@Override
	public void process(Exchange exchange) {
		_exchanges.add(exchange);
		
	}
	
	@Override
	public void run() {
		while (_isActive) {
			try {
				Exchange exchange = _exchanges.take();
				_handlers.handleReceive(Events.createEvent(this, exchange));
			}
			catch (InterruptedException intEx) {
				// signal to interrupt blocking receive - not an error
			}
		}
	}
	
	private void startDelivery() {
		_isActive = true;
		_deliveryThread = new Thread(this);
		_deliveryThread.start();
	}
	
	private void stopDelivery() {
		_isActive = false;
		_deliveryThread.interrupt();
	}

}

