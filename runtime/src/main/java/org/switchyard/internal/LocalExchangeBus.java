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

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerChain;
import org.switchyard.HandlerException;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.handlers.AddressingHandler;
import org.switchyard.handlers.PolicyHandler;
import org.switchyard.handlers.SecurityHandler;
import org.switchyard.handlers.TransactionHandler;
import org.switchyard.handlers.TransformHandler;
import org.switchyard.handlers.ValidateHandler;
import org.switchyard.spi.Dispatcher;
import org.switchyard.spi.ExchangeBus;

/**
 * Default endpoint provider.
 */
public class LocalExchangeBus implements ExchangeBus {

    private ConcurrentHashMap<QName, Dispatcher> _dispatchers = 
        new ConcurrentHashMap<QName, Dispatcher>();
    
    private HandlerChain _requestChain;
    private HandlerChain _replyChain;
    private ServiceDomain _domain;
    
    /**
     * Create a new LocalExchangeBus.
     */
    public LocalExchangeBus() {
    }

    @Override
    public void init(ServiceDomain domain) {
        _domain = domain;
        TransactionHandler transactionHandler = new TransactionHandler();
        SecurityHandler securityHandler = new SecurityHandler(domain.getServiceSecurity());
        TransformHandler transformHandler = new TransformHandler(domain.getTransformerRegistry());
        ValidateHandler validateHandler = new ValidateHandler(domain.getValidatorRegistry());
        
        // Build out the request and reply handler chains.
        _requestChain = new DefaultHandlerChain();
        _requestChain.addLast("addressing", new AddressingHandler(_domain));
        _requestChain.addLast("transaction-pre-invoke", transactionHandler);
        _requestChain.addLast("security", securityHandler);
        _requestChain.addLast("generic-policy", new PolicyHandler());
        _requestChain.addLast("validation-before-transform", validateHandler);
        _requestChain.addLast("transformation", transformHandler);
        _requestChain.addLast("validation-after-transform", validateHandler);
        _requestChain.addLast("provider", new ProviderHandler());

        _requestChain.addLast("transaction-post-invoke", transactionHandler);
        
        _replyChain = new DefaultHandlerChain();
        _replyChain.addLast("validation-before-transform", validateHandler);
        _replyChain.addLast("transformation", transformHandler);
        _replyChain.addLast("validation-after-transform", validateHandler);
        _replyChain.addLast(HandlerChain.CONSUMER_HANDLER, new BaseHandler());
    }

    @Override
    public void start() {
        // NOP
    }

    @Override
    public void stop() {
        _dispatchers.clear();
    }

    @Override
    public synchronized Dispatcher createDispatcher(ServiceReference reference) {
        HandlerChain userHandlers = DefaultHandlerChain.fromList(_domain.getHandlers());
        HandlerChain requestChain = _requestChain.copy();
        HandlerChain replyChain = _replyChain.copy();
        requestChain.addFirst("domain-handlers", userHandlers);
        replyChain.addFirst("domain-handlers", userHandlers);
        
        Dispatcher dispatcher = new LocalDispatcher(reference, requestChain, replyChain);
        _dispatchers.put(reference.getName(), dispatcher);
        
        return dispatcher;
    }

    @Override
    public Dispatcher getDispatcher(ServiceReference reference) {
        return _dispatchers.get(reference.getName());
    }

}

class LocalDispatcher implements Dispatcher {
    private HandlerChain _requestChain;
    private HandlerChain _replyChain;
    private ServiceReference _reference;

    /**
     * Constructor.
     * @param handlerChain handler chain
     */
    LocalDispatcher(final ServiceReference reference, final HandlerChain requestChain, final HandlerChain replyChain) {
        _reference = reference;
        _requestChain = requestChain;
        _replyChain = replyChain;
    }

    @Override
    public void dispatch(final Exchange exchange) {
        switch (exchange.getPhase()) {
        case IN:
            _requestChain.handle(exchange);
            break;
        case OUT:
            ExchangeHandler replyHandler = ((ExchangeImpl)exchange).getReplyHandler();
            if (replyHandler != null) {
                _replyChain.replace(HandlerChain.CONSUMER_HANDLER, replyHandler);
            }
            _replyChain.handle(exchange);
            break;
        default:
            throw new IllegalStateException("Invalid phase for dispatch: " + exchange.getPhase());
        }
    }

    @Override
    public ServiceReference getServiceReference() {
        return _reference;
    }
}

class ProviderHandler extends BaseHandler {

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        exchange.getProvider().getProviderHandler().handleMessage(exchange);
    }
    
}
