/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.internal;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.handlers.HandlerChain;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.transform.TransformSequence;

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

    @Override
    public synchronized void addFirst(String handlerName,
            ExchangeHandler handler) {
        _chain.addFirst(new HandlerRef(handlerName, handler));
    }

    @Override
    public synchronized void addLast(String handlerName,
            ExchangeHandler handler) {
        _chain.addLast(new HandlerRef(handlerName, handler));
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
        try {
            for (HandlerRef ref : listHandlers()) {
                ref.getHandler().handleFault(exchange);
            }
        } catch (Exception e) {
            // This is terminal... a fault on a fault...
            throw new RuntimeException("Unable to recover from fault on", e);
        }
    }

    @Override
    public void handleMessage(Exchange exchange) {
        try {
            for (HandlerRef ref : listHandlers()) {
                ref.getHandler().handleMessage(exchange);

                if (exchange.getState() == ExchangeState.FAULT) {
                    // Exchange state has changed to FAULT.
                    // Stop executing handlers....
                    break;
                }
            }
        } catch (HandlerException handlerEx) {
            _logger.error(handlerEx);

            Message faultMessage = exchange.createMessage().setContent(handlerEx);
            initFaultTransformsequence(exchange, handlerEx, faultMessage);
            exchange.sendFault(faultMessage);
        }
    }

    private void initFaultTransformsequence(Exchange exchange, HandlerException handlerEx, Message faultMessage) {
        ExchangeContract contract = exchange.getContract();
        QName exceptionTypeName = contract.getServiceOperation().getFaultType();
        QName invokerFaultTypeName = contract.getInvokerInvocationMetaData().getFaultType();

        if (exceptionTypeName == null) {
            exceptionTypeName = JavaService.toMessageType(handlerEx.getClass());
        }

        if (exceptionTypeName != null && invokerFaultTypeName != null) {
            // Set up the type info on the message context so as the exception gets transformed
            // appropriately for the invoker...
            TransformSequence.
                from(exceptionTypeName).
                to(invokerFaultTypeName).
                associateWith(exchange, Scope.OUT);
        }
    }
    
    /**
     * Create a copy of the default handler chain.
     * @return copy of the default chain.
     */
    public DefaultHandlerChain copy() {
        return new DefaultHandlerChain(listHandlers());
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

        private final ExchangeHandler _handler;
        private final String _name;
    }
}
