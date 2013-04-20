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
package org.switchyard.bus.camel.processors;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.util.ExchangeHelper;
import org.apache.log4j.Logger;
import org.switchyard.ErrorListener;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Scope;
import org.switchyard.bus.camel.BusLogger;
import org.switchyard.bus.camel.CamelExchange;

/**
 * Processor put at the beginning of OnExceptionDefinition which turns state of
 * SwitchYard exchange into FAULT.
 */
public class ErrorHandlingProcessor implements Processor {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(ErrorHandlingProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        CamelExchange ex = new CamelExchange(exchange);
        if (ex.getState() != ExchangeState.FAULT) {
            Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
            notifyListeners(exchange.getContext(), ex, exception);
            Throwable content = detectHandlerException(exception);
            org.switchyard.Property rollbackOnFaultProperty = ex.getContext().getProperty(org.switchyard.Exchange.ROLLBACK_ON_FAULT);
            if (rollbackOnFaultProperty == null || rollbackOnFaultProperty.getValue() == null) {
                ex.getContext().setProperty(org.switchyard.Exchange.ROLLBACK_ON_FAULT, Boolean.TRUE, Scope.EXCHANGE);
            }
            ex.sendFault(ex.createMessage().setContent(content));
            ExchangeHelper.setFailureHandled(exchange);
        }
    }

    private Throwable detectHandlerException(Throwable throwable) {
        if (throwable instanceof HandlerException) {
            return (HandlerException) throwable;
        }
        return new HandlerException(throwable);
    }

    private void notifyListeners(CamelContext context, org.switchyard.Exchange exchange, Throwable exception) {
        Map<String, ErrorListener> listeners = context.getRegistry().lookupByType(ErrorListener.class);
        if (listeners != null && listeners.size() > 0) {
            for (Entry<String, ErrorListener> entry : listeners.entrySet()) {
                try {
                    entry.getValue().notify(exchange, exception);
                } catch (Exception e) {
                    BusLogger.ROOT_LOGGER.failedToHandlException(entry.getKey(), exception.getClass());
                }
            }
        }
    }

}
