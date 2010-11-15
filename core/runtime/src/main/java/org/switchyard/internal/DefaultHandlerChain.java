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

package org.switchyard.internal;

import java.util.LinkedList;
import java.util.List;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerChain;
import org.switchyard.HandlerException;
import org.switchyard.message.FaultMessage;

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
	public void handleFault(Exchange exchange) {
		try {
			processHandlers(exchange);
		}
		catch (HandlerException handlerEx) {
			// TODO - no throwing exceptions from fault handlers 
			//        need to log this and continue
		}
	}

	@Override
	public void handleMessage(Exchange exchange) throws HandlerException {
		processHandlers(exchange);
	}
	
    @Override
    public void handle(Exchange exchange) {
    	try {
    		processHandlers(exchange);
    	} 
    	catch (HandlerException handlerEx) {
            // TODO - map to fault here
        }
    }

    private void processHandlers(Exchange exchange) throws HandlerException {
        for (HandlerRef ref : listHandlers()) {
        	if (FaultMessage.isFault(exchange.getMessage())) {
        		ref.handler.handleFault(exchange);
        	} 
        	else {
        		ref.handler.handleMessage(exchange);
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
