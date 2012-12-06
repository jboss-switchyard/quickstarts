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
package org.switchyard.component.camel.common.handler;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.exception.SwitchYardException;

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
public class OutboundHandler extends BaseServiceHandler {

    private static Logger _logger = Logger.getLogger(OutboundHandler.class);

    private final MessageComposer<CamelBindingData> _messageComposer;
    private final ProducerTemplate _producerTemplate;
    private final SwitchYardCamelContext _camelContext;
    private final String _uri;

    /**
     * Constructor that will create a default {@link ProducerTemplate}.
     * 
     * @param uri The Camel endpoint uri.
     * @param context The {@link CamelContext}.
     * @param messageComposer the MessageComposer this handler should use
     */
    public OutboundHandler(final String uri, final SwitchYardCamelContext context, MessageComposer<CamelBindingData> messageComposer) {
        this(uri, context, messageComposer, null);
    }

    /**
     * Constructor that allows for specifying a specific {@link ProducerTemplate}.
     * 
     * @param uri The Camel endpoint uri.
     * @param context The {@link CamelContext}.
     * @param messageComposer the MessageComposer this handler should use.
     * @param producerTemplate The {@link ProducerTemplate} to be used by this handler.
     */
    public OutboundHandler(final String uri, final SwitchYardCamelContext context, MessageComposer<CamelBindingData> messageComposer, ProducerTemplate producerTemplate) {
        if (uri == null) {
            throw new IllegalArgumentException("uri argument must not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("camelContext argument must not be null");
        }
        _uri = uri;
        _camelContext = context;
        _messageComposer = messageComposer;
        _producerTemplate = producerTemplate == null ? _camelContext.createProducerTemplate() : producerTemplate;
    }

    /**
     * Starts the {@link ProducerTemplate}.
     */
    @Override
    public void start() {
        try {
            _producerTemplate.start();
            _logger.debug("Started producer template for " + _uri);
        } catch (Exception e) {
            throw new SwitchYardException("Failed to start Camel producer template for " + _uri, e);
        }
    }

    /**
     * Stops the {@link ProducerTemplate}.
     */
    @Override
    public void stop() {
        try {
            _producerTemplate.stop();
            _logger.debug("Stopped producer template for " + _uri);
        } catch (Exception ex) {
            throw new SwitchYardException("Failed to stop Camel producer template for " + _uri, ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        if (isInOnly(exchange)) {
            handleInOnly(exchange);
        } else {
            handleInOut(exchange);
        }
    }

    private boolean isInOnly(final Exchange exchange) {
        return exchange.getContract().getConsumerOperation().getExchangePattern() == ExchangePattern.IN_ONLY;
    }

    private void handleInOnly(final Exchange exchange) throws HandlerException {
        try {
            _producerTemplate.send(_uri, createProcessor(exchange));
        } catch (final CamelExecutionException e) {
            throw new HandlerException(e);
        }
    }

    private void handleInOut(final Exchange switchyardExchange) throws HandlerException {
        final Object payload = sendToCamel(switchyardExchange);
        sendResponseToSwitchyard(switchyardExchange, payload);
    }

    private Object sendToCamel(final Exchange switchyardExchange) throws HandlerException {
        final org.apache.camel.Exchange camelExchange = _producerTemplate.request(_uri, createProcessor(switchyardExchange));
        if (camelExchange.getException() != null) {
            throw new HandlerException(camelExchange.getException());
        }
        return camelExchange.getOut().getBody();
    }

    private void sendResponseToSwitchyard(final Exchange switchyardExchange, final Object payload) {
        switchyardExchange.getMessage().setContent(payload);
        switchyardExchange.send(switchyardExchange.getMessage());
    }

    private Processor createProcessor(final Exchange switchyardExchange) {
        return new DefaultProcessor(_messageComposer, switchyardExchange);
    }

    /**
     * Return the CamelContext used by this handler.
     * @return CamelContext
     */
    public CamelContext getCamelContext() {
        return _camelContext;
    }

    /**
     * Return the Camel endpoint URI used by this handler.
     * @return Camel endpoint URI
     */
    public String getUri() {
        return _uri;
    }

}
