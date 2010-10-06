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

import java.util.LinkedList;
import java.util.List;

import org.jboss.esb.cinco.ExchangeEvent;
import org.jboss.esb.cinco.ExchangeHandler;
import org.jboss.esb.cinco.HandlerChain;
import org.jboss.esb.cinco.HandlerException;
import org.jboss.esb.cinco.internal.event.ExchangeEventImpl;

public class DefaultHandlerChain implements HandlerChain {
	
	private LinkedList<HandlerRef> _chain = new LinkedList<HandlerRef>();
	
	@Override
	public synchronized void addFirst(String handlerName, ExchangeHandler handler) {
		_chain.addFirst(new HandlerRef(handlerName, handler));
	}

	@Override
	public synchronized void addLast(String handlerName, ExchangeHandler handler) {
		_chain.addLast(new HandlerRef(handlerName, handler));
	}
	
	@Override
	public synchronized ExchangeHandler remove(String handlerName) {
		ExchangeHandler handler = null;
		
		for (HandlerRef ref : _chain) {
			if (ref.name.equals(handlerName)) {
				handler = ref.handler;
				_chain.remove(ref);
				break;
			}
		}
		
		return handler;
	}

	@Override
	public void handle(ExchangeEvent event) throws HandlerException {
		handle((ExchangeEventImpl)event);
	}
	
	private void handle(ExchangeEventImpl event) throws HandlerException {
		for (HandlerRef ref : listHandlers()) {
			ref.handler.handle(event);
			// check to see if the last handler asked for a halt
			if (event.isHalted()) {
				break;
			}
		}
	}
	
	private synchronized List<HandlerRef> listHandlers() {
		return new LinkedList<HandlerRef>(_chain);
	}
	
	// sweet little struct
	private class HandlerRef {
		
		HandlerRef(String name,ExchangeHandler handler) {
			this.handler = handler;
			this.name = name;
		}
		
		ExchangeHandler handler;
		String name;
	}
}
