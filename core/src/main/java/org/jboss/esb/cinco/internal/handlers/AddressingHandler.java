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

package org.jboss.esb.cinco.internal.handlers;

import java.util.List;

import org.jboss.esb.cinco.BaseHandler;
import org.jboss.esb.cinco.Direction;
import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.ExchangeState;
import org.jboss.esb.cinco.event.ExchangeInEvent;
import org.jboss.esb.cinco.internal.Events;
import org.jboss.esb.cinco.internal.ExchangeImpl;
import org.jboss.esb.cinco.spi.ServiceRegistry;

public class AddressingHandler extends BaseHandler {
	
	private ServiceRegistry _registry;
	
	public AddressingHandler(ServiceRegistry registry) {
		super(Direction.SEND);
		_registry = registry;
	}

	@Override
	public void exchangeIn(ExchangeInEvent event) {		
		ExchangeImpl exchange = (ExchangeImpl)event.getExchange();
		
		if (exchange.getReceivingChannel() == null) {
			// find the receiving channel
			List<ExchangeChannel> channels = 
				_registry.getChannels(exchange.getService());
			
			if (channels.isEmpty()) {
				// could not find a registered service - set the exchange
				// to error and halt processing of the current event
				event.halt();
				exchange.setError(new Exception(
						"No endpoints for service " + exchange.getService()));
				exchange.setState(ExchangeState.ERROR);
				event.getChannel().getHandlerChain().handle(
						Events.createEvent(event.getChannel(), exchange, Direction.RECEIVE));
				
				return;
			}
			
			// Endpoint selection is arbitrary at the moment
			exchange.setReceivingChannel(channels.get(0));
		}
	}

}
