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
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.SynchronousInOutHandler;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.component.resteasy.resource.Resource;
import org.switchyard.component.resteasy.resource.ResourcePublisherFactory;
import org.switchyard.component.resteasy.util.ClassUtil;
import org.switchyard.deploy.BaseServiceHandler;

/**
 * Hanldes RESTEasy requests to invoke a SwitchYard service.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class InboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(InboundHandler.class);

    private final RESTEasyBindingModel _config;

    private ServiceDomain _domain;
    private ServiceReference _service;
    private Resource _resource;

    /**
     * Constructor.
     * @param config the configuration settings
     * @param domain the service domain
     */
    public InboundHandler(final RESTEasyBindingModel config, ServiceDomain domain) {
        _config = config;
        _domain = domain;
    }

    /**
     * Start lifecycle.
     *
     * @throws RESTEasyPublishException If unable to publish the service
     */
    public void start() throws RESTEasyPublishException {
        String[] resourceIntfs = _config.getInterfacesAsArray();
        try {
            _service = _domain.getServiceReference(_config.getServiceName());
            List<Object> instances = ClassUtil.generateSingletons(resourceIntfs, this);
            String contextPath = _config.getContextPath();
            if (contextPath == null) {
                contextPath = "/";
            }
            // Add as singleton instances
            _resource = ResourcePublisherFactory.getPublisher().publish(contextPath, instances);
        } catch (Exception e) {
            throw new RESTEasyPublishException(e);
        }
    }

    /**
     * Inokes the SwitchYard service.
     *
     * @param operationName the name of the service operation
     * @param input the request parameter
     * @param oneWay true of this is a oneway request
     * @return the response from invocation
     */
    public Object invoke(final String operationName, final Object input, final boolean oneWay) {
        Object response = null;
        try {
            SynchronousInOutHandler inOutHandler = new SynchronousInOutHandler();
            Exchange exchange = _service.createExchange(operationName, inOutHandler);
            Message message = exchange.createMessage();
            message.setContent(input);
            if (oneWay) {
                exchange.send(message);
            } else {
                exchange.send(message);
                exchange = inOutHandler.waitForOut();
                response = exchange.getMessage().getContent();
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
