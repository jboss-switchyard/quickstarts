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
package org.switchyard.bus.camel.audit;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.DelegateAsyncProcessor;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.bus.camel.ExchangeDispatcher;
import org.switchyard.internal.ExchangeImpl;

/**
 * Processor which catches {@link HandlerException} before calling processor.
 * 
 * If an exception occurs 
 */
public class FaultProcessor extends DelegateAsyncProcessor {

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
        HandlerException exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, HandlerException.class);
        ExchangeImpl exc = exchange.getProperty(ExchangeDispatcher.SY_EXCHANGE, ExchangeImpl.class);

        if (exception != null && exc.getState() != ExchangeState.FAULT) {
            // turn state of exchange from OK to FAULT and phase to OUT
            exc.sendFault(exc.createMessage().setContent(exception.isWrapper() ? exception.getCause() : exception));
        }
        return super.process(exchange,callback);
    }

    @Override
    public String toString() {
        return "FaultProcessor";
    }
}
