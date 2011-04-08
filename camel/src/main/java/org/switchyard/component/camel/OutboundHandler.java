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
package org.switchyard.component.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Property;
import org.switchyard.component.camel.config.model.CamelBindingModel;

/**
 * A handler that is capable of calling Apache Camel components and returning responses 
 * from Camel to SwitchYard.
 * <p>
 * The typical usage would be when a POJO has a field with a reference annotation. SwitchYard 
 * can inject a proxy instance which will invoke a Camel endpoint. It is an instance of this 
 * class that will handle the invocation of the Camel endpoint.
 * 
 * @author Daniel Bevenius
 *
 */
public class OutboundHandler extends BaseHandler {
    
    private final ProducerTemplate _producerTemplate;
    private final CamelContext _camelContext;
    private final CamelBindingModel _bindingModel;

    /**
     * Sole constructor.
     * 
     * @param bindingModel The {@link CamelBindingModel}.
     * @param context The {@link CamelContext}.
     */
    public OutboundHandler(final CamelBindingModel bindingModel, final CamelContext context) {
        if (bindingModel == null) {
            throw new IllegalArgumentException("bindingModel argument must not be null");
        }
        _bindingModel = bindingModel;
        
        if (context == null) {
            throw new IllegalArgumentException("camelContext argument must not be null");
        }
        _camelContext = context;
        
        _producerTemplate = _camelContext.createProducerTemplate();
    }
    
    /**
     * Stops the {@link ProducerTemplate}.
     * 
     * @throws Exception If an error occurs while trying to stop the {@link ProducerTemplate}.
     */
    public void stop() throws Exception {
        _producerTemplate.stop();
    }

    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        if (isInOnly(exchange)) {
            handleInOnly(exchange);
        } else {
            handleInOut(exchange);
        }
    }
    
    private boolean isInOnly(final Exchange exchange) {
        return exchange.getContract().getServiceOperation().getExchangePattern() == ExchangePattern.IN_ONLY;
    }

    private void handleInOnly(final Exchange exchange) throws HandlerException {
        try {
            _producerTemplate.send(uriFromBindingModel(), createProcessor(exchange));
        } catch (final CamelExecutionException e) {
            throw new HandlerException(e);
        }
    }
    
    private String uriFromBindingModel() {
        return _bindingModel.getComponentURI().toString();
    }
    
    private void handleInOut(final Exchange exchange) throws HandlerException {
        try {
            final Object payload = sendToCamel(exchange);
            sendResponseToSwitchyard(exchange, payload);
        } catch (final CamelExecutionException e) {
            throw new HandlerException(e);
        }
    }
    
    private Object sendToCamel(final Exchange exchange) {
        final org.apache.camel.Exchange camelExchange = _producerTemplate.request(uriFromBindingModel(), createProcessor(exchange));
        return camelExchange.getOut().getBody();
    }
    
    private void sendResponseToSwitchyard(final Exchange exchange, final Object payload)
    {
        exchange.getMessage().setContent(payload);
        exchange.send(exchange.getMessage());
    }
    
    private Processor createProcessor(final Exchange switchyardExchange) {
        return new Processor() {
            @Override
            public void process(org.apache.camel.Exchange exchange) throws Exception {
                for (Property prop : switchyardExchange.getContext().getProperties()) {
                    exchange.setProperty(prop.getName(), prop.getValue());
                }
                exchange.getIn().setBody(switchyardExchange.getMessage().getContent());
            }
        };
    }
    
}
