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
import org.jboss.esb.cinco.ExchangeEvent;

public class ExchangeEventImpl implements ExchangeEvent {

	private Exchange 	_exchange;
	private Direction	_direction;
	private String		_state;
	private boolean		_halted;
	
	public ExchangeEventImpl(Exchange exchange, String state, Direction direction) {
		_exchange = exchange;
		_state = state;
		_direction = direction;
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
	public String getState() {
		return _state;
	}


	@Override
	public void halt() {
		_halted = true;
	}


	@Override
	public boolean isHalted() {
		return _halted;
	}
}
