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

package org.switchyard.bus.camel;

import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.ModelHelper;
import org.jboss.logging.Logger;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.bus.camel.processors.Processors;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.spi.Dispatcher;
import org.switchyard.spi.ExchangeBus;

/**
 * Exchange bus implemented on to of Apache Camel mediation engine. SwitchYard
 * handlers are wrapped into camel Exchange processors.
 */
public class CamelExchangeBus implements ExchangeBus {

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

        SimpleRegistry registry = _camelContext.getWritebleRegistry();
        for (Processors processor : Processors.values()) {
            registry.put(processor.name(), processor.create(domain));
        }
    }

    /**
     * Start the bus provider.
     */
    @Override
    public synchronized void start() {
        try {
            _camelContext.start();
        } catch (Exception ex) {
            throw BusMessages.MESSAGES.failedToStartBus(ex);
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
            throw BusMessages.MESSAGES.failedToStopBus(ex);
        }
    }

    @Override
    public Dispatcher getDispatcher(ServiceReference reference) {
        return _dispatchers.get(reference.getName());
    }

    @Override
    public ExchangeDispatcher createDispatcher(final ServiceReference reference) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Creating Camel dispatcher for " + reference.getName());
        }

        String endpoint = "direct:" + reference.getName();
        RouteBuilder rb = new CamelExchangeBusRouteBuilder(endpoint, reference);

        try {
            // TODO - remove this logic once the test framework is able 
            // to hot-replace a service endpoint.
            if (_camelContext.getRoute(endpoint) != null) {
                _camelContext.removeRoute(endpoint);
                if (_logger.isInfoEnabled()) {
                    BusLogger.ROOT_LOGGER.removeRoute(endpoint);
                }
            }

            _camelContext.addRoutes(rb);

            if (_logger.isDebugEnabled()) {
                _logger.debug("Created route for " + endpoint + ", definition is: " + ModelHelper.dumpModelAsXml(rb.getRouteCollection()));
            }
        } catch (Exception ex) {
            throw BusMessages.MESSAGES.failedToCreateRoute(reference.getName(), ex);
        }

        ExchangeDispatcher dispatcher = new ExchangeDispatcher(_camelContext, reference);
        _dispatchers.put(reference.getName(), dispatcher);
        return dispatcher;
    }

}

