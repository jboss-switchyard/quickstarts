/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.camel.component;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;

/**
 * A CamelResponseHandler is responsible for passing back result data from Apache Camel data to
 * SwitchYard.
 * 
 * By given access to the CamelExchange this ExchangeHandler can extract the SwitchYard payload
 * and set in into the Camel Exchange.
 * 
 * @author Daniel Bevenius
 *
 */
public class CamelResponseHandler implements ExchangeHandler {
    
    private final org.apache.camel.Exchange _camelExchange;

    /**
     * Sole constructor.
     * 
     * @param camelExchange The Camel {@link org.apache.camel.Exchange}
     */
    public CamelResponseHandler(final org.apache.camel.Exchange camelExchange) {
        this._camelExchange = camelExchange;
    }

    /**
     * Will extract the message content from the SwitchYard exchange and insert
     * it into the Camel Exchange's In body.
     * 
     * @param switchYardExchange SwitchYards Exchange from which the payload will be extracted.
     * @throws HandlerException If there was an exception while trying to extract the payload from 
     * the SwitchYard Exchange.
     */
    @Override
    public void handleMessage(final Exchange switchYardExchange) throws HandlerException {
        final Object payload = switchYardExchange.getMessage().getContent();
        _camelExchange.getIn().setBody(payload);
    }

    @Override
    public void handleFault(final Exchange exchange) {
        //TODO: Implement fault handling.
    }

}
