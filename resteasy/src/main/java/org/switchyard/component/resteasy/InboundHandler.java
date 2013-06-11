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
 
package org.switchyard.component.resteasy;

import java.util.List;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.SynchronousInOutHandler;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.resteasy.composer.RESTEasyComposition;
import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
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
     */
    public RESTEasyBindingData invoke(final RESTEasyBindingData restMessageRequest, final boolean oneWay) {
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
            return output;
        }
        if (oneWay) {
            exchange.send(message);
        } else {
            exchange.send(message);
            exchange = inOutHandler.waitForOut();
            try {
                output = _messageComposer.decompose(exchange, output);
            } catch (Exception e) {
                LOGGER.error("Unexpected exception composing outbound REST response", e);
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
