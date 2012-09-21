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
package org.switchyard.bus.camel.processors;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.bus.camel.ExchangeDispatcher;
import org.switchyard.handlers.TransformHandler;
import org.switchyard.internal.ExchangeImpl;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.transform.TransformSequence;

/**
 * Processor used to call reply chain.
 */
public class ConsumerCallbackProcessor implements Processor {

    private TransformHandler _transform;

    /**
     * Creates new consumer callback processor.
     * 
     * @param transform Handler used to transform reply in case of failures.
     */
    public ConsumerCallbackProcessor(TransformHandler transform) {
        _transform = transform;
    }

    @Override
    public void process(Exchange ex) throws Exception {
        ExchangeImpl syEx = ex.getProperty(
                ExchangeDispatcher.SY_EXCHANGE, org.switchyard.internal.ExchangeImpl.class);

        // Did we fail?
        HandlerException error = ex.getProperty(Exchange.EXCEPTION_CAUGHT, HandlerException.class);
        if (error != null) {
            handleFault(syEx, error);
        } else {
            syEx.getReplyHandler().handleMessage(syEx);
        }
    }

    private void handleFault(ExchangeImpl exchange, HandlerException ex) {
        Throwable error = ex.isWrapper() ? ex.getCause() : ex;
        Message faultMessage = exchange.createMessage().setContent(error);
        exchange.sendFault(faultMessage);

        ExchangeContract contract = exchange.getContract();
        QName exceptionTypeName = null;
        if (contract.getProviderOperation() != null) {
            exceptionTypeName = contract.getProviderOperation().getFaultType();
        }
        QName invokerFaultTypeName = contract.getConsumerOperation().getFaultType();

        // no fault defined on provider interface
        if (exceptionTypeName == null) {
            exceptionTypeName = JavaService.toMessageType(error.getClass());
        }

        if (exceptionTypeName != null && invokerFaultTypeName != null) {
            // Set up the type info on the message context so as the exception gets transformed
            // appropriately for the invoker...
            TransformSequence.
                from(exceptionTypeName).
                to(invokerFaultTypeName).
                associateWith(exchange, Scope.OUT);
            _transform.handleFault(exchange);
        }

        exchange.getReplyHandler().handleFault(exchange);
    }
}
