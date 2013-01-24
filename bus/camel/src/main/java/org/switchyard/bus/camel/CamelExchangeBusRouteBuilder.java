/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.ErrorHandlerBuilder;
import org.apache.camel.builder.LoggingErrorHandlerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ExpressionNode;
import org.apache.camel.model.FilterDefinition;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.InterceptStrategy;
import org.switchyard.ExchangePattern;
import org.switchyard.bus.camel.audit.AuditInterceptStrategy;
import org.switchyard.bus.camel.audit.FaultInterceptStrategy;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.ServiceOperation;

/**
 * Route builder which creates mediation necessary to handle communication inside SwitchYard.
 */
public class CamelExchangeBusRouteBuilder extends RouteBuilder {

    private static final Predicate IN_OUT_CHECK = new Predicate() {
        @Override
        public boolean matches(Exchange exchange) {
            org.switchyard.Exchange syEx = CamelHelper.getSwitchYardExchange(exchange);
            ServiceOperation operation = syEx.getContract().getConsumerOperation();
            return operation.getExchangePattern() == ExchangePattern.IN_OUT;
        }

        public String toString() {
            return "IN_OUT_CHECK";
        }
    };

    private String _endpoint;

    /**
     * Dedicated route builder which dynamically creates SwitchYard mediation
     * from given endpoint.
     * 
     * @param endpoint Endpoint address.
     */
    public CamelExchangeBusRouteBuilder(String endpoint) {
        this._endpoint = endpoint;
    }

    @Override
    public void configure() throws Exception {
        RouteDefinition definition = from(_endpoint);
        definition.routeId(_endpoint);

        Map<String, ErrorHandlerBuilder> handlers = getContext().getRegistry().lookupByType(ErrorHandlerBuilder.class);
        if (handlers != null && !handlers.isEmpty()) {
            if (handlers.size() == 1) {
                definition.errorHandler(handlers.values().iterator().next());
            } else {
                throw new SwitchYardException("Only one exception handler can be defined. Found " + handlers.keySet());
            }
        } else {
            // set up default error handler without re-delivery
            LoggingErrorHandlerBuilder errorHandler = loggingErrorHandler(CamelExchangeBus.class.getName());
            errorHandler.setLevel(LoggingLevel.DEBUG);
            definition.errorHandler(errorHandler);
        }

        definition.addInterceptStrategy(new FaultInterceptStrategy());
        // add default intercept strategy using @Audit annotation
        definition.addInterceptStrategy(new AuditInterceptStrategy());

        Map<String, InterceptStrategy> interceptStrategies = getContext().getRegistry().lookupByType(InterceptStrategy.class);
        if (interceptStrategies != null) {
            for (Entry<String, InterceptStrategy> interceptEntry : interceptStrategies.entrySet()) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding intercept strategy {} to route {}", interceptEntry.getKey(), _endpoint);
                }
                definition.addInterceptStrategy(interceptEntry.getValue());
            }
        }

        final ExpressionNode filterDefinition = new FilterDefinition(IN_OUT_CHECK)
            .processRef(DOMAIN_HANDLERS.name())
            .processRef(VALIDATION.name())
            .processRef(TRANSFORMATION.name())
            .processRef(VALIDATION.name())
            .processRef(CONSUMER_CALLBACK.name());

        OnExceptionDefinition onException = new OnExceptionDefinition(Throwable.class);
        onException.handled(true);
        onException.addOutput(filterDefinition);
        // register exception closure
        definition.addOutput(onException);

        definition
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
            .addOutput(filterDefinition);
    }

}
