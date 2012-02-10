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
package org.switchyard.component.camel;

import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.ServiceReference;
import org.switchyard.composer.MessageComposer;
import org.switchyard.exception.SwitchYardException;

/**
 * A CamelResponseHandler is responsible for passing back result data from Apache Camel to
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
    private final MessageComposer<org.apache.camel.Message> _messageComposer;

    /**
     * Sole constructor.
     * 
     * @param camelExchange The Camel {@link org.apache.camel.Message}
     * @param reference The SwitchYard ServiceReference.
     * @param messageComposer the MessageComposer to use
     */
    public CamelResponseHandler(final org.apache.camel.Exchange camelExchange, final ServiceReference reference, final MessageComposer<org.apache.camel.Message> messageComposer) {
        if (camelExchange ==  null) {
            throw new SwitchYardException("[camelExchange] argument must not be null");
        }
        if (reference == null) {
            throw new SwitchYardException("[reference] argument must not be null");
        }
        _camelExchange = camelExchange;
        _messageComposer = messageComposer;
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
        try {
            Message camelMsg = _camelExchange.getPattern().equals(ExchangePattern.InOnly)
                    ? _camelExchange.getIn() : _camelExchange.getOut();
            _messageComposer.decompose(switchYardExchange, camelMsg);
        } catch (Exception e) {
            throw new HandlerException(e);
        }
    }

    @Override
    public void handleFault(final Exchange exchange) {
        final Object content = exchange.getMessage().getContent();
        if (content instanceof Throwable) {
            _camelExchange.setException((Throwable)content);
        }
    }

}
