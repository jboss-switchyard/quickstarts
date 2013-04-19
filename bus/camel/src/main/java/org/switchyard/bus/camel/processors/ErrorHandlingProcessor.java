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
package org.switchyard.bus.camel.processors;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.util.ExchangeHelper;
import org.apache.log4j.Logger;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Scope;
import org.switchyard.bus.camel.CamelExchange;
import org.switchyard.bus.camel.ErrorListener;

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
                    LOG.error("Error listener " + entry.getKey() + " failed to handle exception " + exception.getClass());
                }
            }
        }
    }

}
