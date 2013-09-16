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
 
package org.switchyard.component.http;

import java.util.Set;

import org.jboss.logging.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.component.common.SynchronousInOutHandler;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.common.selector.OperationSelectorFactory;
import org.switchyard.component.http.composer.HttpBindingData;
import org.switchyard.component.http.composer.HttpComposition;
import org.switchyard.component.http.composer.HttpRequestBindingData;
import org.switchyard.component.http.composer.HttpResponseBindingData;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.component.http.endpoint.Endpoint;
import org.switchyard.component.http.endpoint.EndpointPublisherFactory;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.runtime.event.ExchangeCompletionEvent;
import org.switchyard.security.SecurityContext;
import org.switchyard.selector.OperationSelector;

/**
 * Hanldes HTTP requests to invoke a SwitchYard service.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class InboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(InboundHandler.class);

    private final HttpBindingModel _config;
    private final String _gatewayName;
    private ServiceDomain _domain;
    private ServiceReference _serviceRef;
    private MessageComposer<HttpBindingData> _messageComposer;
    private final OperationSelector<HttpBindingData> _operationSelector;
    private Endpoint _endpoint;

    /**
     * Constructor.
     * @param config the configuration settings
     * @param domain the service domain
     */
    public InboundHandler(final HttpBindingModel config, ServiceDomain domain) {
        super(domain);
        _config = config;
        _gatewayName = config.getName();
        _domain = domain;
        _operationSelector = OperationSelectorFactory
                .getOperationSelectorFactory(HttpBindingData.class)
                .newOperationSelector(config.getOperationSelector());
    }

    /**
     * Start lifecycle.
     *
     * @throws HttpPublishException If unable to publish the service
     */
    @Override
    protected void doStart() throws HttpPublishException {
        try {
            _serviceRef = _domain.getServiceReference(_config.getServiceName());
            String contextPath = _config.getContextPath();
            if (contextPath == null) {
                contextPath = "/";
            }
            _endpoint = EndpointPublisherFactory.getPublisher().publish(contextPath, this);
            // Create and configure the HTTP message composer
            _messageComposer = HttpComposition.getMessageComposer(_config);
        } catch (Exception e) {
            throw HttpMessages.MESSAGES.unableToPublish(e);
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
            Exchange exchange = _serviceRef.createExchange(getOperationName(input), inOutHandler);

            // identify ourselves
            exchange.getContext().setProperty(ExchangeCompletionEvent.GATEWAY_NAME, _gatewayName, Scope.EXCHANGE)
                    .addLabels(BehaviorLabel.TRANSIENT.label());

            Message message = _messageComposer.compose(input, exchange);
            SecurityContext.get(exchange).getCredentials().addAll(input.extractCredentials());
            if (exchange.getContract().getConsumerOperation().getExchangePattern() == ExchangePattern.IN_ONLY) {
                exchange.send(message);
                response = new HttpResponseBindingData();
            } else {
                exchange.send(message);
                exchange = inOutHandler.waitForOut();
                response = (HttpResponseBindingData) _messageComposer.decompose(exchange, new HttpResponseBindingData());
            }
        } catch (Exception e) {
            HttpLogger.ROOT_LOGGER.unexpectedExceptionInvokingSwitchyardServcie(e);
        }
        return response;
    }

    /**
     * Stop lifecycle.
     */
    @Override
    protected void doStop() {
        _endpoint.stop();
    }

    @Override
    public void handleFault(Exchange exchange) {
        throw HttpMessages.MESSAGES.unexpectedFault();
    }

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        throw HttpMessages.MESSAGES.unexpectedMessage();
    }

    private String getOperationName(HttpRequestBindingData message) throws Exception {
        String operationName = null;
        if (_operationSelector != null) {
            operationName = _operationSelector.selectOperation(message).getLocalPart();
        }
        
        if (operationName == null) {
            final Set<ServiceOperation> operations = _serviceRef.getInterface().getOperations();
            if (operations.size() != 1) {
                throw HttpMessages.MESSAGES.moreThanOneOperationSelector(operations);
            }
            final ServiceOperation serviceOperation = operations.iterator().next();
            operationName = serviceOperation.getName();
        }
        
        return operationName;
        
    }
}
