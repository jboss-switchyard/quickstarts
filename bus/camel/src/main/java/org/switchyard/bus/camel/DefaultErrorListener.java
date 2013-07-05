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

import org.apache.log4j.Logger;
import org.switchyard.ErrorListener;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.common.lang.Strings;
import org.switchyard.runtime.util.ExchangeFormatter;

/**
 * Default error listener.
 */
public class DefaultErrorListener implements ErrorListener {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(DefaultErrorListener.class);

    @Override
    public void notify(Exchange exchange, Throwable throwable) {
        ExchangePattern pattern = exchange.getContract().getConsumerOperation().getExchangePattern();
        
        String message = String.format("%s\nCaught exception of type %s with message: %s", ExchangeFormatter.format(exchange, false), throwable.getClass().getName(), throwable.getMessage());
        String causeTrace = "";

        if (throwable.getCause() != null) {
            String causedBy = "\n%sCaused by exception of type %s, message: %s";
            Throwable cause = throwable.getCause();
            int depth = 0;
            while (cause != null) {
                causeTrace += String.format(causedBy, Strings.repeat("  ", ++depth), cause.getClass().getName(), cause.getMessage());
                cause = cause.getCause();
            }
        }

        if (pattern == ExchangePattern.IN_ONLY) {
            LOG.error(message + causeTrace, throwable);
        } else {
            LOG.debug(message + causeTrace, throwable);
        }
    }

}
