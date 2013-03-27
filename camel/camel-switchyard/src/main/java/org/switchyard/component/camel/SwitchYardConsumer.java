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
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.exception.SwitchYardException;

/**
 * A SwitchYardConsumer is both a Camel Consumer and an SwitchYard ExchangeHandler.
 * </p>
 * A Camel event driven consumer that is able to receive events, SwitchYard Exchanges, 
 * and invoke the Camel processors.
 * 
 * @author Daniel Bevenius
 */
public class SwitchYardConsumer extends DefaultConsumer implements ServiceHandler {

    private final MessageComposer<CamelBindingData> _messageComposer;

    /**
     * Sole constructor.
     * 
     * @param endpoint The Camel endpoint that this consumer was created by.
     * @param processor The Camel processor that this consumer will delegate to.
     * @param messageComposer the MessageComposer this consumer should use
     */
    public SwitchYardConsumer(final Endpoint endpoint, final Processor processor, final MessageComposer<CamelBindingData> messageComposer) {
        super(endpoint, processor);
        _messageComposer = messageComposer;
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

    @Override
    public void start() {
        try {
            super.start();
        } catch (Exception ex) {
            throw new SwitchYardException(ex);
        }
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } catch (Exception ex) {
            throw new SwitchYardException(ex);
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
    }

    private org.apache.camel.Exchange createCamelExchange(final Exchange switchyardExchange) {
        org.apache.camel.Exchange camelExchange = isInOut(switchyardExchange) 
                ? getEndpoint().createExchange(org.apache.camel.ExchangePattern.InOut)
                : getEndpoint().createExchange(org.apache.camel.ExchangePattern.InOnly);
         try {
             _messageComposer.decompose(switchyardExchange, new CamelBindingData(camelExchange.getIn()));
         } catch (Exception e) {
             throw new SwitchYardException(e);
         }
        return camelExchange;
    }

    private void sendResponse(org.apache.camel.Exchange camelExchange, final Exchange switchyardExchange) {
        final org.apache.camel.Message camelMessage;
        if (camelExchange.hasOut()) {
            camelMessage = camelExchange.getOut();
        } else {
            camelMessage = camelExchange.getIn();
        }
        Message switchyardMessage;
        try {
            switchyardMessage = _messageComposer.compose(new CamelBindingData(camelMessage), switchyardExchange, true);
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
        switchyardExchange.send(switchyardMessage);
    }

    private boolean isInOut(final Exchange exchange) {
        return exchange.getContract().getProviderOperation().getExchangePattern() == ExchangePattern.IN_OUT;
    }

    @Override
    public void handleFault(final Exchange exchange) {
        //TODO: Implement error handling.
    }

}
