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

package org.switchyard.runtime.event;

import java.util.EventObject;

import org.switchyard.Exchange;

/**
 * Fired when an exchange has completed.  For InOnly exchanges, this fires after
 * the in message has been processed.  For InOut exchanges, this fires after the
 * out message has been sent. 
 */
public class ExchangeCompletionEvent extends EventObject {

    /**
     * Exchange property name used to record the duration of a completed exchange.
     */
    public static final String EXCHANGE_DURATION = "org.switchyard.exchangeDurationMS";
    /**
     * Exchange property name used to record the name of the gateway being
     * invoked, if any.
     */
    public static final String GATEWAY_NAME = "org.switchyard.exchangeGatewayName";

    private static final long serialVersionUID = 1L;
    
    /**
     * Create a new ExchangeCompletionEvent.
     * @param exchange the exchange which has completed
     */
    public ExchangeCompletionEvent(Exchange exchange) {
        super(exchange);
    }

    /**
     * Gets the completed exchange.
     * @return the completed exchange
     */
    public Exchange getExchange() {
        return (Exchange)getSource();
    }
}
