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

package org.switchyard.bus.camel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.log4j.Logger;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.handlers.AddressingHandler;
import org.switchyard.handlers.PolicyHandler;
import org.switchyard.handlers.TransactionHandler;
import org.switchyard.handlers.TransformHandler;
import org.switchyard.handlers.ValidateHandler;
import org.switchyard.internal.ExchangeImpl;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.spi.Dispatcher;
import org.switchyard.spi.ExchangeBus;
import org.switchyard.transform.TransformSequence;

/**
 * Exchange bus implemented on to of Apache Camel mediation engine. SwitchYard
 * handlers are wrapped into camel Exchange processors.
 */
public class CamelExchangeBus implements ExchangeBus {

    private static final String IN_OUT_CHECK = 
            "${property.SwitchYardExchange.contract.consumerOperation.exchangePattern} == 'IN_OUT'";

    private Logger _logger = Logger.getLogger(CamelExchangeBus.class);

    private HashMap<QName, ExchangeDispatcher> _dispatchers = 
        new HashMap<QName, ExchangeDispatcher>();

    private SwitchYardCamelContext _camelContext;

    /**
     * Create a new Camel exchange bus provider.
     * @param context the CamelContext instance used by this provider
     */
    public CamelExchangeBus(SwitchYardCamelContext context) {
        _camelContext = context;
    }

    @Override
    public void init(ServiceDomain domain) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Initialization of CamelExchangeBus for domain " + domain.getName());
        }

        // Create our handler Processors
        TransactionHandler transactionHandler = new TransactionHandler();
        ValidateHandler validateHandler = new ValidateHandler(domain.getValidatorRegistry());
        TransformHandler transformHandler = new TransformHandler(domain.getTransformerRegistry());

        SimpleRegistry registry = _camelContext.getWritebleRegistry();
        registry.put("domain-handlers", new HandlerProcessor(domain.getHandlers()));
        registry.put("addressing", new HandlerProcessor(new AddressingHandler(domain)));
        registry.put("transaction-handler", new HandlerProcessor(transactionHandler));
        registry.put("generic-policy", new HandlerProcessor(new PolicyHandler()));
        registry.put("validation", new HandlerProcessor(validateHandler));
        registry.put("transformation", new HandlerProcessor(transformHandler));
        registry.put("consumer-callback", new ConsumerCallbackProcessor(transformHandler));
    }

    /**
     * Start the bus provider.
     */
    @Override
    public synchronized void start() {
        try {
            _camelContext.start();
        } catch (Exception ex) {
            throw new SwitchYardException("Failed to start Camel Exchange Bus", ex);
        }
    }

    /**
     * Stop the provider.
     */
    @Override
    public synchronized void stop() {
        try {
            _dispatchers.clear();
            _camelContext.stop();
        } catch (Exception ex) {
            throw new SwitchYardException("Failed to stop Camel Exchange Bus", ex);
        }
    }

    @Override
    public Dispatcher getDispatcher(ServiceReference reference) {
        return _dispatchers.get(reference.getName());
    }

    @Override
    public Dispatcher createDispatcher(final ServiceReference reference) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Creating Camel dispatcher for " + reference.getName());
        }

        final String endpoint = "direct:" + reference.getName();

        RouteBuilder rb = new RouteBuilder() {
            public void configure() throws Exception {
                from(endpoint).routeId(endpoint)
                    .onException(HandlerException.class).processRef("consumer-callback").handled(true).end()
                    .processRef("domain-handlers")
                    .processRef("addressing")
                    .processRef("transaction-handler")
                    .processRef("generic-policy")
                    .processRef("validation")
                    .processRef("transformation")
                    .processRef("validation")
                    .process(new ProviderProcessor())
                    .processRef("transaction-handler")
                    .filter().simple(IN_OUT_CHECK)
                        .processRef("domain-handlers")
                        .processRef("validation")
                        .processRef("transformation")
                        .processRef("validation")
                        .processRef("consumer-callback");
            }
        };

        try {
            // TODO - remove this logic once the test framework is able 
            // to hot-replace a service endpoint.
            if (_camelContext.getRoute(endpoint) != null) {
                _camelContext.removeRoute(endpoint);
                if (_logger.isInfoEnabled()) {
                    _logger.info("Removing route " + endpoint);
                }
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("Created route for " + endpoint + ", definition is: " + rb.toString());
            }

            _camelContext.addRoutes(rb);
        } catch (Exception ex) {
            throw new SwitchYardException("Failed to create Camel route for service " + reference.getName(), ex);
        }

        ExchangeDispatcher dispatcher = new ExchangeDispatcher(
                reference, _camelContext.createProducerTemplate());
        _dispatchers.put(reference.getName(), dispatcher);
        return dispatcher;
    }

}

class HandlerProcessor implements Processor {

    private List<ExchangeHandler> _handlers;
    
    HandlerProcessor(ExchangeHandler handler) {
        _handlers = new ArrayList<ExchangeHandler>();
        _handlers.add(handler);
    }
    
    HandlerProcessor(List<ExchangeHandler> handlers) {
        _handlers = handlers;
    }
    
    @Override
    public void process(Exchange ex) throws Exception {
        for (ExchangeHandler handler : _handlers) {
            handler.handleMessage(ex.getProperty(
                    ExchangeDispatcher.SY_EXCHANGE, org.switchyard.Exchange.class));
        }
    }
    
}

class ProviderProcessor implements Processor {
    @Override
    public void process(Exchange ex) throws Exception {
        org.switchyard.Exchange syEx = ex.getProperty(
                ExchangeDispatcher.SY_EXCHANGE, org.switchyard.Exchange.class);
        syEx.getProvider().getProviderHandler().handleMessage(ex.getProperty(
                ExchangeDispatcher.SY_EXCHANGE, org.switchyard.Exchange.class));
    }
}

class ConsumerCallbackProcessor implements Processor {
    
    private TransformHandler _transform;
    
    ConsumerCallbackProcessor(TransformHandler transform) {
        _transform = transform;
    }
    
    @Override
    public void process(Exchange ex) throws Exception {
        ExchangeImpl syEx = ex.getProperty(
                ExchangeDispatcher.SY_EXCHANGE, org.switchyard.internal.ExchangeImpl.class);
        
        // Did we fail?
        HandlerException error = ex.getProperty(Exchange.EXCEPTION_CAUGHT, HandlerException.class);
        if (error != null) {
            handleFault(syEx, error);
        } else {
            syEx.getReplyHandler().handleMessage(syEx);
        }
    }
    
    private void handleFault(ExchangeImpl exchange, HandlerException ex) {
        Throwable error = ex.isWrapper() ? ex.getCause() : ex;
        Message faultMessage = exchange.createMessage().setContent(error);
        exchange.sendFault(faultMessage);
        
        ExchangeContract contract = exchange.getContract();
        QName exceptionTypeName = null;
        if (contract.getProviderOperation() != null) {
            exceptionTypeName = contract.getProviderOperation().getFaultType();
        }
        QName invokerFaultTypeName = contract.getConsumerOperation().getFaultType();

        // no fault defined on provider interface
        if (exceptionTypeName == null) {
            exceptionTypeName = JavaService.toMessageType(error.getClass());
        }

        if (exceptionTypeName != null && invokerFaultTypeName != null) {
            // Set up the type info on the message context so as the exception gets transformed
            // appropriately for the invoker...
            TransformSequence.
                from(exceptionTypeName).
                to(invokerFaultTypeName).
                associateWith(exchange, Scope.OUT);
            _transform.handleFault(exchange);
        }
        
        exchange.getReplyHandler().handleFault(exchange);
    }
}
