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

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
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
        SecurityHandler securityHandler = new SecurityHandler();
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
        _replyChain.addLast("security", securityHandler);
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
        HandlerChain requestChain = _requestChain.copy();
        HandlerChain replyChain = _replyChain.copy();
        
        Dispatcher dispatcher = new LocalDispatcher(_domain, reference, requestChain, replyChain);
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
    private ServiceDomain _domain;

    /**
     * Constructor.
     * @param _domain 
     * @param handlerChain handler chain
     */
    LocalDispatcher(ServiceDomain domain, final ServiceReference reference, final HandlerChain requestChain, final HandlerChain replyChain) {
        this._domain = domain;
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

    @Override
    public Exchange createExchange(ExchangeHandler handler, ExchangePattern pattern) {
        ExchangeImpl exchangeImpl = new ExchangeImpl(_domain, this, handler);
        return exchangeImpl;
    }
}

class ProviderHandler extends BaseHandler {

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        exchange.getProvider().getProviderHandler().handleMessage(exchange);
    }
    
}
