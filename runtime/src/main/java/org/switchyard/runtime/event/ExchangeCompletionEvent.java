/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
