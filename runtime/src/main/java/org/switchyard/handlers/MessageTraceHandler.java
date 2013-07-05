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

package org.switchyard.handlers;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.runtime.util.ExchangeFormatter;

/**
 * Half-baked message tracing implementation.
 */
public class MessageTraceHandler implements ExchangeHandler {
    
    /**
     * Property name used to enable message tracing.
     */
    public static final String TRACE_ENABLED = 
            "org.switchyard.handlers.messageTrace.enabled";
    
    private static Logger _log = Logger.getLogger(MessageTraceHandler.class);

    @Override
    public void handleFault(Exchange exchange) {
        if (_log.isInfoEnabled()) {
            _log.info(ExchangeFormatter.format(exchange, false));
        }
    }

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        if (_log.isInfoEnabled()) {
            _log.info(ExchangeFormatter.format(exchange, true));
        }
    }

}
