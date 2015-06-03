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

import java.util.Arrays;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangeState;
import org.switchyard.bus.camel.CamelExchange;

/**
 * Processor wrapping SwitchYard {@link ExchangeHandler} invocation.
 */
public class HandlerProcessor implements Processor {

    /**
     * Wrapped handlers.
     */
    private List<ExchangeHandler> _handlers;

    /**
     * Creates new processor which delegates execution to SwitchYard {@link ExchangeHandler}.
     * 
     * @param handler Handler to wrap.
     */
    public HandlerProcessor(ExchangeHandler handler) {
        this(Arrays.asList(handler));
    }

    /**
     * Creates new processor which delegates execution to collection of exchange handlers.
     * 
     * @param handlers Exchange handlers used to process messages during dispatching.
     */
    public HandlerProcessor(List<ExchangeHandler> handlers) {
        _handlers = handlers;
    }

    @Override
    public void process(Exchange ex) throws Exception {
        org.switchyard.Exchange exchange = new CamelExchange(ex);

        for (ExchangeHandler handler : _handlers) {
            if (exchange.getState() == ExchangeState.FAULT) {
                handler.handleFault(exchange);
            } else {
                handler.handleMessage(exchange);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("HandlerProcessor [%s]", _handlers);
    }

}
