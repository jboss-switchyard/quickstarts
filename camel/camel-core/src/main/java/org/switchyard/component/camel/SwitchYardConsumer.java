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

import org.apache.camel.Endpoint;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;

/**
 * A SwitchYardConsumer is both a Camel Consumer and an SwitchYard ExchangeHandler.
 * </p>
 * A Camel event driven consumer that is able to receive events, SwitchYard Exchanges, 
 * and invoke the Camel processors.
 * 
 * @author Daniel Bevenius
 *
 */
public class SwitchYardConsumer extends DefaultConsumer implements ExchangeHandler {
    
    /**
     * Sole constructor.
     * 
     * @param endpoint The Camel endpoint that this consumer was created by.
     * @param processor The Camel processor that this consumer will delegate to.
     */
    public SwitchYardConsumer(final Endpoint endpoint, final Processor processor) {
        super(endpoint, processor);
    }
    
    @Override
    public void handleMessage(final Exchange switchyardExchange) throws HandlerException {
        final org.apache.camel.Exchange camelExchange = createCamelExchange(switchyardExchange);
        invokeCamelProcessor(camelExchange);
        
        handleExceptionsFromCamel(camelExchange);
            
        if (isInOut(switchyardExchange)) {
            sendResponse(camelExchange, switchyardExchange);
        }
    }
    
    private void invokeCamelProcessor(final org.apache.camel.Exchange camelExchange) throws HandlerException {
        try {
            getProcessor().process(camelExchange);
        } catch (final Exception e) {
            throw new HandlerException(e); 
        }
    }
    
    private void handleExceptionsFromCamel(final org.apache.camel.Exchange camelExchange) throws HandlerException {
        final Exception camelException = camelExchange.getException();
        if (camelException != null) {
            throw new HandlerException(camelException);
        }

        final Message message = camelExchange.getOut();
        if (message.isFault()) {
            throw new HandlerException(message.getBody(String.class));
        }
    }
    
    private org.apache.camel.Exchange createCamelExchange(final Exchange switchyardExchange) {
        org.apache.camel.Exchange camelExchange = isInOut(switchyardExchange) 
                ? getEndpoint().createExchange(org.apache.camel.ExchangePattern.InOut)
                : getEndpoint().createExchange(org.apache.camel.ExchangePattern.InOnly);
        
        camelExchange.getIn().setBody(switchyardExchange.getMessage().getContent());
        return camelExchange;
        
    }

    private void sendResponse(org.apache.camel.Exchange camelExchange, final Exchange switchyardExchange) {
        Object payload = camelExchange.getOut().getBody();
        if (payload == null) {
            payload = camelExchange.getIn().getBody();
        }
        switchyardExchange.getMessage().setContent(payload);
        switchyardExchange.send(switchyardExchange.getMessage());
    }

    private boolean isInOut(final Exchange exchange) {
        return exchange.getContract().getServiceOperation().getExchangePattern() == ExchangePattern.IN_OUT;
    }
    
    @Override
    public void handleFault(final Exchange exchange) {
        //TODO: Implement error handling.
    }
    
}
