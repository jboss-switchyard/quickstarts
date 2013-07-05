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
package org.switchyard.bus.camel.audit;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.DelegateAsyncProcessor;
import org.apache.log4j.Logger;
import org.switchyard.bus.camel.CamelExchange;

/**
 * This processor catches exceptions from camel thrown during handling fault replies
 * from switchyard.
 * 
 * If exception occurs during fault handling it will be ignored.
 */
public class FaultProcessor extends DelegateAsyncProcessor {

    private Logger _logger = Logger.getLogger(FaultProcessor.class);

    /**
     * Creates new fault processor.
     * 
     * @param target Wrapped processor.
     */
    public FaultProcessor(Processor target) {
        super(target);
    }

    @Override
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
        return super.process(exchange, new AsyncCallback() {
            @Override
            public void done(boolean doneSync) {
                if (doneSync) { // verify exchange only if processing is done
                    if (CamelExchange.isFault(exchange) && exchange.getException() != null) {
                        handle(exchange.getException(), exchange);
                        exchange.setException(null);
                    }
                }
                callback.done(doneSync);
            }
        });
    }

    /**
     * Method which tries to send FAULT if there is exception reported by processor
     * and exchange state is still OK.
     * 
     * @param throwable Exception thrown by target processor.
     * @param camel Camel exchange. 
     * @param exchange SwitchYard exchange related to exception.
     */
    protected void handle(Throwable throwable, Exchange exchange) {
        // exception thrown during handling FAULT state cannot be forwarded
        // anywhere, because we already have problem to handle
        _logger.error("Unexpected exception thrown during handling FAULT response. "
            + "This exception can not be handled, thus it's marked as handled and only logged. "
            + "If you don't want see messages like this consider handling "
            + "exceptions in your handler logic", throwable);
    }

    @Override
    public String toString() {
        return "FaultProcessor [" + getProcessor() + "]";
    }

}
