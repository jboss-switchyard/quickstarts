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
 
package org.switchyard.component.resteasy;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.component.common.SynchronousInOutHandler;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
import org.switchyard.component.resteasy.composer.RESTEasyComposition;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.component.resteasy.resource.Resource;
import org.switchyard.component.resteasy.resource.ResourcePublisherFactory;
import org.switchyard.component.resteasy.util.ClassUtil;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * Handles RESTEasy requests to invoke a SwitchYard service.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class InboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(InboundHandler.class);

    private final RESTEasyBindingModel _config;
    private final String _gatewayName;
    private ServiceDomain _domain;
    private ServiceReference _service;
    private Resource _resource;
    private MessageComposer<RESTEasyBindingData> _messageComposer;

    /**
     * Constructor.
     * @param config the configuration settings
     * @param domain the service domain
     */
    public InboundHandler(final RESTEasyBindingModel config, ServiceDomain domain) {
        super(domain);
        _config = config;
        _gatewayName = config.getName();
        _domain = domain;
    }

    /**
     * Start lifecycle.
     *
     * @throws RESTEasyPublishException If unable to publish the service
     */
    @Override
    protected void doStart() throws RESTEasyPublishException {
        String[] resourceIntfs = _config.getInterfacesAsArray();
        try {
            _service = _domain.getServiceReference(_config.getServiceName());
            List<Object> instances = ClassUtil.generateSingletons(resourceIntfs, this);
            String contextPath = _config.getContextPath();
            if ((contextPath == null) || (ResourcePublisherFactory.ignoreContext())) {
                contextPath = "/";
            }
            // Add as singleton instances
            _resource = ResourcePublisherFactory.getPublisher().publish(contextPath, instances);
            // Create and configure the RESTEasy message composer
            _messageComposer = RESTEasyComposition.getMessageComposer(_config);
        } catch (Exception e) {
            throw new RESTEasyPublishException(e);
        }
    }

    /**
     * Invokes the SwitchYard service.
     *
     * @param restMessageRequest the request RESTEasyMessage
     * @param oneWay true of this is a oneway request
     * @return the response from invocation
     * @throws WebApplicationException if any error
     */
    public RESTEasyBindingData invoke(final RESTEasyBindingData restMessageRequest, final boolean oneWay) throws WebApplicationException {
        RESTEasyBindingData output = new RESTEasyBindingData();
        SynchronousInOutHandler inOutHandler = new SynchronousInOutHandler();
        Exchange exchange = _service.createExchange(restMessageRequest.getOperationName(), inOutHandler);

        // identify ourselves
        exchange.getContext().setProperty(ExchangeCompletionEvent.GATEWAY_NAME, _gatewayName, Scope.EXCHANGE)
                .addLabels(BehaviorLabel.TRANSIENT.label());

        Message message = null;
        try {
            message = _messageComposer.compose(restMessageRequest, exchange);
        } catch (Exception e) {
            LOGGER.error("Unexpected exception composing inbound Message", e);
            throw new WebApplicationException(e);
        }
        if (oneWay) {
            exchange.send(message);
        } else {
            exchange.send(message);
            exchange = inOutHandler.waitForOut();
            try {
                output = _messageComposer.decompose(exchange, output);
            } catch (WebApplicationException wae) {
                throw wae;
            } catch (Exception e) {
                LOGGER.error("Unexpected exception composing outbound REST response", e);
                throw new WebApplicationException(e);
            }
        }
        return output;
    }

    /**
     * Stop lifecycle.
     */
    @Override
    protected void doStop() {
        _resource.stop();
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
