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
 * Fired when an exchange is containing the request is being dispatched for processing. 
 * For InOnly and InOut exchanges, this event fires before
 * the in message has been processed.  
 */
public class ExchangeInitiatedEvent extends EventObject {   
    /**
     * Exchange property name used to timestamp the initiation of a dispatched exchange.
     */
    public static final String EXCHANGE_INITIATED_TIME = "org.switchyard.exchangeInitiatedNS";
    private static final long serialVersionUID = 1L;
    
    /**
     * Create a new ExchangeInitiatedEvent.
     * @param exchange the exchange to be initiated
     */
    public ExchangeInitiatedEvent(Exchange exchange) {
        super(exchange);
    }

    /**
     * Gets the initiated exchange.
     * @return the initiated exchange
     */
    public Exchange getExchange() {
        return (Exchange)getSource();
    }
}
