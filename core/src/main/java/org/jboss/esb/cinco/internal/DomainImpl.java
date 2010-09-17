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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.Direction;
import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.HandlerChain;
import org.jboss.esb.cinco.ServiceDomain;
import org.jboss.esb.cinco.internal.handlers.AddressingHandler;
import org.jboss.esb.cinco.internal.handlers.DeliveryHandler;
import org.jboss.esb.cinco.spi.ServiceRegistry;

public class DomainImpl implements ServiceDomain {
	
	private ExecutorService _executor;
	private HandlerChain _systemHandlers;
	private ServiceRegistry _registry;
	private PriorityBlockingQueue<ExchangeChannel> _channels = 
		new PriorityBlockingQueue<ExchangeChannel>();
	private volatile boolean _started;

	public DomainImpl() {
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
		
		// If the domain is started, we need to schedule a delivery thread
		synchronized (this) {
			if (_started) {
				_executor.submit(new Delivery(channel));
			}
		}
		return channel;
	}
	

	@Override
	public synchronized void start() {
		if (_started == true) {
			return;
		}
		
		// TODO : this needs to be configurable and the current strategy of
		//        one thread per channel is pretty hacky
		_executor = Executors.newFixedThreadPool(50);
		for (ExchangeChannel channel : _channels) {
			_executor.submit(new Delivery(channel));
		}
		_started = true;
	}
	
	@Override
	public synchronized void stop() {
		if (_started != true) {
			return;
		}
		
		_started = false;
		// TODO : handle this better - shutdown, await, etc.
		_executor.shutdownNow();
	}
	

	@Override
	public void registerService(QName serviceName, ExchangeChannel channel) {
		_registry.registerService(serviceName, channel);
		_channels.put(channel);
	}


	@Override
	public void unregisterService(QName serviceName, ExchangeChannel channel) {
		_registry.unregisterService(serviceName, channel);
		_channels.remove(channel);

	}
	
	class Delivery implements Runnable {

		private ExchangeChannelImpl _channel;
		
		Delivery(ExchangeChannel channel) {
			_channel = (ExchangeChannelImpl)channel;
		}
		
		@Override
		public void run() {
			while (_started) {
				try {
					Exchange ex = _channel.receive();
					_channel.getHandlerChain().handle(
							Events.createEvent(_channel, ex, Direction.RECEIVE));
				}
				catch (InterruptedException intEx) {
					// signal to interrupt blocking receive - not an error
				}
			}
		}
		
		
	}
}
