/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
 
package org.switchyard.component.http;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.SynchronousInOutHandler;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.http.composer.HttpComposition;
import org.switchyard.component.http.composer.HttpBindingData;
import org.switchyard.component.http.composer.HttpRequestBindingData;
import org.switchyard.component.http.composer.HttpResponseBindingData;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.component.http.endpoint.Endpoint;
import org.switchyard.component.http.endpoint.EndpointPublisherFactory;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.ExchangePattern;

/**
 * Hanldes HTTP requests to invoke a SwitchYard service.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class InboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(InboundHandler.class);

    private final HttpBindingModel _config;

    private ServiceDomain _domain;
    private ServiceReference _service;
    private MessageComposer<HttpBindingData> _messageComposer;
    private String _operationName;
    private Endpoint _endpoint;

    /**
     * Constructor.
     * @param config the configuration settings
     * @param domain the service domain
     */
    public InboundHandler(final HttpBindingModel config, ServiceDomain domain) {
        _config = config;
        _domain = domain;
    }

    /**
     * Start lifecycle.
     *
     * @throws HttpPublishException If unable to publish the service
     */
    public void start() throws HttpPublishException {
        try {
            _service = _domain.getServiceReference(_config.getServiceName());
            _operationName = _config.getOperationName();
            String contextPath = _config.getContextPath();
            if (contextPath == null) {
                contextPath = "/";
            }
            _endpoint = EndpointPublisherFactory.getPublisher().publish(contextPath, this);
            // Create and configure the HTTP message composer
            _messageComposer = HttpComposition.getMessageComposer(_config);
        } catch (Exception e) {
            throw new HttpPublishException(e);
        }
    }

    /**
     * Invokes the SwitchYard service.
     *
     * @param input the HTTP request message
     * @return the HTTP response message from invocation
     */
    public HttpResponseBindingData invoke(final HttpRequestBindingData input) {
        HttpResponseBindingData response = null;
        try {
            SynchronousInOutHandler inOutHandler = new SynchronousInOutHandler();
            Exchange exchange = _service.createExchange(_operationName, inOutHandler);
            Message message = _messageComposer.compose(input, exchange, true);
            if (exchange.getContract().getConsumerOperation().getExchangePattern() == ExchangePattern.IN_ONLY) {
                exchange.send(message);
                response = new HttpResponseBindingData();
            } else {
                exchange.send(message);
                exchange = inOutHandler.waitForOut();
                response = (HttpResponseBindingData) _messageComposer.decompose(exchange, new HttpResponseBindingData());
            }
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
        return response;
    }

    /**
     * Stop lifecycle.
     */
    public void stop() {
        _endpoint.stop();
    }

    @Override
    public void handleFault(Exchange exchange) {
        throw new IllegalStateException("Unexpected");
    }

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        throw new IllegalStateException("Unexpected");
    }
}
