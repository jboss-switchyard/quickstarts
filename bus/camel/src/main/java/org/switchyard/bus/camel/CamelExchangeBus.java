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
import java.util.ServiceLoader;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.PackageScanClassResolver;
import org.apache.camel.spi.Registry;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.exception.SwitchYardException;
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
 * 
 */
public class CamelExchangeBus implements ExchangeBus {

    private static final String IN_OUT_CHECK = 
            "${property.SwitchYardExchange.contract.serviceOperation.exchangePattern} == 'IN_OUT'";
    
    private HashMap<QName, ExchangeDispatcher> _dispatchers = 
        new HashMap<QName, ExchangeDispatcher>();
    
    private CamelContext _camelContext;
    
    /**
     * Create a new Camel exchange bus provider.
     */
    public CamelExchangeBus() {
    }
    
    @Override
    public void init(ServiceDomain domain) {
        // Create our handler Processors
        TransactionHandler transactionHandler = new TransactionHandler();
        ValidateHandler validateHandler = new ValidateHandler(domain.getValidatorRegistry());
        TransformHandler transformHandler = new TransformHandler(domain.getTransformerRegistry());
        SimpleRegistry processors = new SimpleRegistry();
        processors.put("domain-handlers", new HandlerProcessor(domain.getHandlers()));
        processors.put("transaction-handler", new HandlerProcessor(transactionHandler));
        processors.put("generic-policy", new HandlerProcessor(new PolicyHandler()));
        processors.put("validation", new HandlerProcessor(validateHandler));
        processors.put("transformation", new HandlerProcessor(transformHandler));
        processors.put("consumer-callback", new ConsumerCallbackProcessor(transformHandler));
        
        _camelContext =  new DefaultCamelContext(createRegistry(processors));
    
        final PackageScanClassResolver packageScanClassResolver = getPackageScanClassResolver();
        if (packageScanClassResolver != null) {
            _camelContext.setPackageScanClassResolver(packageScanClassResolver);
        }
    }
    
    /**
     * Start the bus provider.
     */
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
    public synchronized void stop() {
        try {
            _dispatchers.clear();
            _camelContext.stop();
        } catch (Exception ex) {
            throw new SwitchYardException("Failed to stop Camel Exchange Bus", ex);
        }
    }

    @Override
    public Dispatcher getDispatcher(Service service) {
        return _dispatchers.get(service.getName());
    }

    @Override
    public Dispatcher createDispatcher(final Service service, final ExchangeHandler serviceHandler) {
        final String endpoint = "direct:" + service.getName();
        
        RouteBuilder rb = new RouteBuilder() {
            public void configure() throws Exception {
                from(endpoint).routeId(endpoint)
                    .onException(HandlerException.class).processRef("consumer-callback").handled(true).end()
                    .processRef("domain-handlers")
                    .processRef("transaction-handler")
                    .processRef("generic-policy")
                    .processRef("validation")
                    .processRef("transformation")
                    .processRef("validation")
                    .process(new HandlerProcessor(serviceHandler))
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
            }
            _camelContext.addRoutes(rb);
        } catch (Exception ex) {
            throw new SwitchYardException("Failed to create Camel route for service " + service.getName(), ex);
        }
        
        ExchangeDispatcher dispatcher = new ExchangeDispatcher(
                service, _camelContext.createProducerTemplate());
        _dispatchers.put(service.getName(), dispatcher);
        return dispatcher;
    }

    /**
     * Get the first PackageScanClassResolver Service found on the classpath.
     * @return The first PackageScanClassResolver Service found on the classpath.
     */
    public  PackageScanClassResolver getPackageScanClassResolver() {
        final ServiceLoader<PackageScanClassResolver> resolverLoaders = 
                ServiceLoader.load(PackageScanClassResolver.class, this.getClass().getClassLoader());
    
        for (PackageScanClassResolver packageScanClassResolver : resolverLoaders) {
            return packageScanClassResolver;
        }
    
        return null;
    }
    
    // This code is ripped off from the camel component
    private Registry createRegistry(Registry processors) {
        final ServiceLoader<Registry> registriesLoaders = ServiceLoader.load(Registry.class, getClass().getClassLoader());
        final List<Registry> registries = new ArrayList<Registry>();
        registries.add(new JndiRegistry());
        registries.add(processors);

        for (Registry registry : registriesLoaders) {
            registries.add(registry);
        }
        
        return new CompositeRegistry(registries);
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
        QName exceptionTypeName = contract.getServiceOperation().getFaultType();
        QName invokerFaultTypeName = contract.getInvokerInvocationMetaData().getFaultType();

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
