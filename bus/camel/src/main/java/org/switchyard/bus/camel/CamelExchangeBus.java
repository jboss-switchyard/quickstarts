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

import static org.switchyard.bus.camel.processors.Processors.ADDRESSING;
import static org.switchyard.bus.camel.processors.Processors.CONSUMER_CALLBACK;
import static org.switchyard.bus.camel.processors.Processors.DOMAIN_HANDLERS;
import static org.switchyard.bus.camel.processors.Processors.GENERIC_POLICY;
import static org.switchyard.bus.camel.processors.Processors.PROVIDER_CALLBACK;
import static org.switchyard.bus.camel.processors.Processors.SECURITY;
import static org.switchyard.bus.camel.processors.Processors.TRANSACTION_HANDLER;
import static org.switchyard.bus.camel.processors.Processors.TRANSFORMATION;
import static org.switchyard.bus.camel.processors.Processors.VALIDATION;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.InterceptStrategy;
import org.apache.log4j.Logger;
import org.switchyard.HandlerException;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.bus.camel.audit.AuditInterceptStrategy;
import org.switchyard.bus.camel.processors.Processors;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.spi.Dispatcher;
import org.switchyard.spi.ExchangeBus;

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
                RouteDefinition definition = from(endpoint);
                definition.routeId(endpoint);

                // add default intercept strategy using @Audit annotation
                definition.addInterceptStrategy(new AuditInterceptStrategy());
                Map<String, InterceptStrategy> interceptStrategies = _camelContext.getRegistry().lookupByType(InterceptStrategy.class);
                if (interceptStrategies != null) {
                    for (Entry<String, InterceptStrategy> interceptEntry : interceptStrategies.entrySet()) {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("Adding intercept strategy " + interceptEntry.getKey() + " to route " + endpoint);
                        }
                        definition.addInterceptStrategy(interceptEntry.getValue());
                    }
                }

                definition.onException(HandlerException.class).processRef(CONSUMER_CALLBACK.name()).handled(true).end()
                    .processRef(DOMAIN_HANDLERS.name())
                    .processRef(ADDRESSING.name())
                    .processRef(TRANSACTION_HANDLER.name())
                    .processRef(SECURITY.name())
                    .processRef(GENERIC_POLICY.name())
                    .processRef(VALIDATION.name())
                    .processRef(TRANSFORMATION.name())
                    .processRef(VALIDATION.name())
                    .processRef(PROVIDER_CALLBACK.name())
                    .processRef(TRANSACTION_HANDLER.name())
                    .filter().simple(IN_OUT_CHECK)
                        .processRef(DOMAIN_HANDLERS.name())
                        .processRef(VALIDATION.name())
                        .processRef(TRANSFORMATION.name())
                        .processRef(VALIDATION.name())
                        .processRef(CONSUMER_CALLBACK.name());
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

