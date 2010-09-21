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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.esb.cinco.Direction;
import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.ExchangeState;
import org.jboss.esb.cinco.HandlerChain;

public class ExchangeChannelImpl implements ExchangeChannel {
	
	private HandlerChain _handlers = new DefaultHandlerChain();
	private BlockingQueue<Exchange> _exchanges = 
		new LinkedBlockingQueue<Exchange>();
	
	ExchangeChannelImpl() {
	}


	@Override
	public HandlerChain getHandlerChain() {
		return _handlers;
	}

	@Override
	public void send(Exchange exchange) {
		send((ExchangeImpl)exchange);
	}


	public void deliver(Exchange exchange) {
		_exchanges.offer(exchange);
	}
	
	public Exchange receive() throws InterruptedException {
		return _exchanges.take();
	}
	

	@Override
	public Exchange createExchange(ExchangePattern pattern) {
		return new ExchangeImpl(pattern);
	}
	
	void send(ExchangeImpl exchange) {
		ExchangeState nextState = ExchangeUtil.nextState(exchange);
		if (nextState != null) {
			exchange.setState(nextState);
			_handlers.handle(Events.createEvent(this, exchange, Direction.SEND));
		}
	}

}

