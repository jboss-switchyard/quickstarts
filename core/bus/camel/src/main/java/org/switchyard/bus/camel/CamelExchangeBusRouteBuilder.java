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

import static org.switchyard.bus.camel.processors.Processors.ADDRESSING;
import static org.switchyard.bus.camel.processors.Processors.CONSUMER_CALLBACK;
import static org.switchyard.bus.camel.processors.Processors.CONSUMER_INTERCEPT;
import static org.switchyard.bus.camel.processors.Processors.ERROR_HANDLING;
import static org.switchyard.bus.camel.processors.Processors.GENERIC_POLICY;
import static org.switchyard.bus.camel.processors.Processors.PROVIDER_CALLBACK;
import static org.switchyard.bus.camel.processors.Processors.PROVIDER_INTERCEPT;
import static org.switchyard.bus.camel.processors.Processors.SECURITY_CLEANUP;
import static org.switchyard.bus.camel.processors.Processors.SECURITY_PROCESS;
import static org.switchyard.bus.camel.processors.Processors.TRANSACTION_HANDLER;
import static org.switchyard.bus.camel.processors.Processors.TRANSFORMATION;
import static org.switchyard.bus.camel.processors.Processors.VALIDATION;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.ErrorHandlerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ExpressionNode;
import org.apache.camel.model.FilterDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.TryDefinition;
import org.apache.camel.spi.InterceptStrategy;
import org.switchyard.ErrorListener;
import org.switchyard.ExchangePattern;
import org.switchyard.ServiceReference;
import org.switchyard.bus.camel.audit.AuditInterceptStrategy;
import org.switchyard.bus.camel.audit.FaultInterceptStrategy;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.metadata.qos.Throttling;

/**
 * Route builder which creates mediation necessary to handle communication inside SwitchYard.
 */
public class CamelExchangeBusRouteBuilder extends RouteBuilder {

    private static final Predicate IN_OUT_CHECK = new Predicate() {
        @Override
        public boolean matches(Exchange exchange) {
            ServiceOperation operation = new CamelExchange(exchange).getContract().getConsumerOperation();
            return operation.getExchangePattern() == ExchangePattern.IN_OUT;
        }

        public String toString() {
            return "IN_OUT_CHECK";
        }
    };
    
    private static final Predicate THROTTLE_CHECK = new Predicate() {
        @Override
        public boolean matches(Exchange exchange) {
            return exchange.getIn().getHeader(Throttling.MAX_REQUESTS) != null;
        }

        public String toString() {
            return "THROTTLE_CHECK";
        }
    };

    private String _endpoint;
    private ServiceReference _reference;

    /**
     * Dedicated route builder which dynamically creates SwitchYard mediation
     * from given endpoint.
     * 
     * @param reference ServiceReference representing the consumer
     * @param endpoint Endpoint address.
     */
    public CamelExchangeBusRouteBuilder(String endpoint, ServiceReference reference) {
        _endpoint = endpoint;
        _reference = reference;
    }

    @Override
    public SwitchYardCamelContext getContext() {
        return (SwitchYardCamelContext) super.getContext();
    }

    @Override
    public void configure() throws Exception {
        RouteDefinition definition = from(_endpoint);
        definition.routeId(_endpoint);

        Map<String, ErrorHandlerBuilder> handlers = lookup(ErrorHandlerBuilder.class);
        if (handlers.isEmpty()) {
            definition.errorHandler(loggingErrorHandler());
        } else if (handlers.size() == 1) {
            definition.errorHandler(handlers.values().iterator().next());
        } else {
            throw BusMessages.MESSAGES.maxOneExceptionHandler(handlers.keySet());
        }

        // add default intercept strategy using @Audit annotation
        definition.addInterceptStrategy(new FaultInterceptStrategy());
        definition.addInterceptStrategy(new AuditInterceptStrategy());

        for (Entry<String, InterceptStrategy> interceptEntry : lookup(InterceptStrategy.class).entrySet()) {
            if (log.isDebugEnabled()) {
                log.debug("Adding intercept strategy {} to route {}", interceptEntry.getKey(), _endpoint);
            }
            definition.addInterceptStrategy(interceptEntry.getValue());
        }

        Map<String, ErrorListener> errorListeners = lookup(ErrorListener.class);
        if (errorListeners.isEmpty()) {
            getContext().getWritebleRegistry().put("defaultErrorListener", new DefaultErrorListener());
        }

        // Since camel doesn't support onException closures together with doCatch/doFinal
        // code below is commented because it doesn't work as expected
        // definition.onException(Throwable.class).processRef(FATAL_ERROR.name());

        TryDefinition tryDefinition = definition.doTry();
        addThrottling(tryDefinition);
        
        tryDefinition
            .processRef(CONSUMER_INTERCEPT.name())
            .processRef(ADDRESSING.name())
            .processRef(TRANSACTION_HANDLER.name())
            .processRef(SECURITY_PROCESS.name())
            .processRef(GENERIC_POLICY.name())
            .processRef(VALIDATION.name())
            .processRef(TRANSFORMATION.name())
            .processRef(VALIDATION.name())
            .processRef(PROVIDER_INTERCEPT.name())
            .processRef(PROVIDER_CALLBACK.name())
            .processRef(PROVIDER_INTERCEPT.name())
            .processRef(SECURITY_CLEANUP.name())
            .processRef(TRANSACTION_HANDLER.name())
            .addOutput(createFilterDefinition());
        
        tryDefinition
            .doCatch(Exception.class)
            .processRef(ERROR_HANDLING.name())
            .processRef(PROVIDER_INTERCEPT.name())
            .processRef(SECURITY_CLEANUP.name())
            .processRef(TRANSACTION_HANDLER.name())
            .addOutput(createFilterDefinition());
        
        tryDefinition.doFinally()
            .processRef(CONSUMER_INTERCEPT.name())
            .processRef(CONSUMER_CALLBACK.name());
    }

    private ExpressionNode createFilterDefinition() {
        return new FilterDefinition(IN_OUT_CHECK)
            .processRef(VALIDATION.name())
            .processRef(TRANSFORMATION.name())
            .processRef(VALIDATION.name());
    }

    /**
     * Lookup in camel context given type of beans.
     * 
     * @param type Type of bean.
     * @return Map of beans where key is name.
     */
    private <T> Map<String, T> lookup(Class<T> type) {
        Map<String, T> result = getContext().getRegistry().lookupByType(type);
        if (result == null) {
            return Collections.emptyMap();
        }
        return result;
    }
    
    private void addThrottling(TryDefinition route) {
        Throttling throttling = _reference.getServiceMetadata().getThrottling();
        long timePeriodMS = throttling != null ? throttling.getTimePeriod() : Throttling.DEFAULT_TIME_PERIOD;
        route.filter(THROTTLE_CHECK)
            .throttle(header(Throttling.MAX_REQUESTS)).timePeriodMillis(timePeriodMS)
            // throttle needs a child process, so we'll just remove the header
            // using an empty process definition causes some of the interceptors
            // to blow chunks, specifically audit interceptors
            .removeHeader(Throttling.MAX_REQUESTS)
            .end().end();
    }
}
