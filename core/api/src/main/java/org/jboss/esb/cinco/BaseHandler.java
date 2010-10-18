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

package org.jboss.esb.cinco;

import org.jboss.esb.cinco.event.ExchangeFaultEvent;
import org.jboss.esb.cinco.event.ExchangeInEvent;
import org.jboss.esb.cinco.event.ExchangeOutEvent;


public abstract class BaseHandler implements ExchangeHandler {
	
	private boolean _sendEnabled;
	private boolean _recvEnabled;
	
	public BaseHandler() {
		_sendEnabled = true;
		_recvEnabled = true;
	}
	
	public BaseHandler(Direction ... eventFilter) {
		for (Direction d : eventFilter) {
			if (d.equals(Direction.RECEIVE)) {
				_recvEnabled = true;
			}
			else if (d.equals(Direction.SEND)) {
				_sendEnabled = true;
			}
		}
	}

	@Override
	public void handle(ExchangeEvent event) throws HandlerException {
		switch (event.getDirection()) {
		case SEND :
			if (_sendEnabled) {
				dispatchEvent(event);
			}
			break;
		case RECEIVE :
			if (_recvEnabled) {
				dispatchEvent(event);
			}
			break;
		}
	}
	
	
	public void exchangeIn(ExchangeInEvent event) throws HandlerException {
		// NOP - handled by subclasses
	}
	
	public void exchangeOut(ExchangeOutEvent event) throws HandlerException {
		// NOP - handled by subclasses
	}
	
	public void exchangeFault(ExchangeFaultEvent event) throws HandlerException {
		// NOP - handled by subclasses
	}

	public void dispatchEvent(ExchangeEvent event) throws HandlerException {
		if (event instanceof ExchangeInEvent) {
			exchangeIn((ExchangeInEvent)event);
		}
		else if (event instanceof ExchangeOutEvent) {
			exchangeOut((ExchangeOutEvent)event);
		}
		else if (event instanceof ExchangeFaultEvent) {
			exchangeFault((ExchangeFaultEvent)event);
		}
	}
}
