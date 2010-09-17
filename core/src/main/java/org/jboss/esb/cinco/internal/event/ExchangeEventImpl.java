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

package org.jboss.esb.cinco.internal.event;

import org.jboss.esb.cinco.Direction;
import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.ExchangeEvent;
import org.jboss.esb.cinco.ExchangeState;
import org.jboss.esb.cinco.internal.ExchangeImpl;

public class ExchangeEventImpl implements ExchangeEvent {

	private ExchangeChannel _channel;
	private Exchange _exchange;
	private Direction _direction;
	private boolean _halted;
	
	public ExchangeEventImpl(
			ExchangeChannel channel, Exchange exchange, Direction direction) {
		_channel = channel;
		_exchange = exchange;
		_direction = direction;
	}
	
	@Override
	public ExchangeChannel getChannel() {
		return _channel;
	}

	@Override
	public Exchange getExchange() {
		return _exchange;
	}

	@Override
	public Direction getDirection() {
		return _direction;
	}

	@Override
	public ExchangeState getState() {
		return ((ExchangeImpl)_exchange).getState();
	}

	@Override
	public void halt() {
		_halted = true;
	}

	public boolean isHalted() {
		return _halted;
	}
}
