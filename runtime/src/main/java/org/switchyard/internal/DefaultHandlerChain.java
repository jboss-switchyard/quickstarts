/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.switchyard.internal;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.runtime.RuntimeLogger;

/**
 * Default handler chain.
 */
public class DefaultHandlerChain implements HandlerChain {
    private static Logger _logger = Logger.getLogger(DefaultHandlerChain.class);
    private final LinkedList<HandlerRef> _chain = new LinkedList<HandlerRef>();
    
    /**
     * Create a new handler chain with no handlers in it.
     */
    public DefaultHandlerChain() {
    }
    
    /**
     * Create a new handler chain with the specified handlers.  This ctor
     * is not intended for external use - it's used by the clone() method.
     * @param handlers
     */
    private DefaultHandlerChain(List<HandlerRef> handlers) {
        _chain.addAll(handlers);
    }

    /**
     * Create a handler chain from a list of ExchangeHandler instances.  The 
     * handlers in the chain are named using the class name of the handler.
     * @param handlers list of handlers
     * @return new HandlerChain containing the list of handlers in order
     */
    public static DefaultHandlerChain fromList(List<ExchangeHandler> handlers) {
        DefaultHandlerChain chain = new DefaultHandlerChain();
        for (ExchangeHandler handler : handlers) {
            chain.addLast(handler.getClass().getName(), handler);
        }
        return chain;
    }

    @Override
    public synchronized void addFirst(String handlerName,
            ExchangeHandler handler) {
        HandlerRef handlerRef = new HandlerRef(handlerName, handler);

        _chain.addFirst(handlerRef);
        if (_logger.isDebugEnabled()) {
            _logger.debug("Added ExchangeHandler instance at start of Handler Chain: " + handlerRef);
        }
    }

    @Override
    public synchronized void addLast(String handlerName,
            ExchangeHandler handler) {
        HandlerRef handlerRef = new HandlerRef(handlerName, handler);

        _chain.addLast(handlerRef);
        if (_logger.isDebugEnabled()) {
            _logger.debug("Added ExchangeHandler instance at end of Handler Chain: " + handlerRef);
        }
    }
    
    @Override
    public synchronized boolean replace(String handlerName, ExchangeHandler handler) {
        for (int i = 0; i < _chain.size(); i++) {
            if (_chain.get(i).getName().equals(handlerName)) {
                _chain.remove(i);
                _chain.add(i, new HandlerRef(handlerName, handler));
                return true;
            }
        }
        
        // no handler with the specified name found
        return false;
    }

    @Override
    public synchronized ExchangeHandler remove(String handlerName) {
        ExchangeHandler handler = null;

        for (HandlerRef ref : _chain) {
            if (ref.getName().equals(handlerName)) {
                handler = ref.getHandler();
                _chain.remove(ref);
                break;
            }
        }

        return handler;
    }

    @Override
    public void handle(Exchange exchange) {
        if (exchange.getState() == ExchangeState.FAULT) {
            handleFault(exchange);
        } else {
            handleMessage(exchange);
        }
    }

    @Override
    public void handleFault(Exchange exchange) {
        for (HandlerRef ref : listHandlers()) {
            try {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Executing Fault ExchangeHandler (" + ref + ") on message Exchange instance (" + System.identityHashCode(exchange) + ").");
                }
                ref.getHandler().handleFault(exchange);
            } catch (Exception e) {
                RuntimeLogger.ROOT_LOGGER.handlerFailedHandleFault(ref.getName(), e);
            }
        }
    }

    @Override
    public void handleMessage(Exchange exchange) {
        HandlerRef handlerRef = null;
        try {
            for (HandlerRef ref : listHandlers()) {
                handlerRef = ref;
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Executing ExchangeHandler (" + ref + ") on message Exchange instance (" + System.identityHashCode(exchange) + ").");
                }
                ref.getHandler().handleMessage(exchange);

                if (exchange.getState() == ExchangeState.FAULT) {
                    // Exchange state has changed to FAULT.
                    // Stop executing handlers....
                    break;
                }
            }
        } catch (HandlerException handlerEx) {
            String errorMsg = handlerEx.getClass().getName() + " was thrown by handler(" + handlerRef.getName() + "): " + handlerEx.getMessage();
            Throwable cause = handlerEx;
            while ((cause = cause.getCause()) != null) {
                errorMsg += " --- Caused by " + cause.getClass().getName() + ": " + cause.getMessage();
            }
            _logger.error(errorMsg);
            _logger.debug("", handlerEx);

            Message faultMessage = exchange.createMessage().setContent(handlerEx);
            exchange.sendFault(faultMessage);
        }
    }
    
    @Override
    public List<ExchangeHandler> getHandlers() {
        List<ExchangeHandler> handlers = new LinkedList<ExchangeHandler>();
        for (HandlerRef hr : listHandlers()) {
            handlers.add(hr.getHandler());
        }
        return Collections.unmodifiableList(handlers);
    }
    
    /**
     * Create a copy of the default handler chain.
     * @return copy of the default chain.
     */
    public DefaultHandlerChain copy() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Cloning DefaultHandlerChain from a its list of Handlers: " + listHandlers());
        }
        return new DefaultHandlerChain(listHandlers());
    }

    @Override
    public String toString() {
        return _chain.toString();
    }

    private synchronized List<HandlerRef> listHandlers() {
        return new LinkedList<HandlerRef>(_chain);
    }

    // sweet little struct
    private static final class HandlerRef {
        HandlerRef(String name, ExchangeHandler handler) {
            _handler = handler;
            _name = name;
        }

        public String getName() {
            return _name;
        }

        public ExchangeHandler getHandler() {
            return _handler;
        }

        @Override
        public String toString() {
            return "Name '" + _name + "',  Class '" + _handler.getClass().getName() + "'";
        }

        private final ExchangeHandler _handler;
        private final String _name;
    }
}
