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

import static org.switchyard.Exchange.FAULT_TYPE;
import static org.switchyard.Exchange.OPERATION_NAME;
import static org.switchyard.Exchange.SERVICE_NAME;

import javax.activation.DataHandler;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultMessage;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.common.camel.ContextPropertyUtil;
import org.switchyard.common.camel.HandlerDataSource;
import org.switchyard.common.camel.SwitchYardMessage;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.metadata.ServiceOperation;

/**
 * A SwitchYardConsumer is both a Camel Consumer and an SwitchYard ExchangeHandler.
 * </p>
 * A Camel event driven consumer that is able to receive events, SwitchYard Exchanges, 
 * and invoke the Camel processors.
 * 
 * @author Daniel Bevenius
 */
public class SwitchYardConsumer extends DefaultConsumer implements ServiceHandler {

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
        org.apache.camel.Exchange camelExchange = getEndpoint().createExchange(isInOut(switchyardExchange) ? org.apache.camel.ExchangePattern.InOut : org.apache.camel.ExchangePattern.InOnly);
        DefaultMessage targetMessage = new SwitchYardMessage();
        targetMessage.setBody(switchyardExchange.getMessage().getContent());

        for (Property property : switchyardExchange.getContext().getProperties()) {
            if (property.hasLabel(BehaviorLabel.TRANSIENT.label()) || ContextPropertyUtil.isReservedProperty(property.getName(), property.getScope())) {
                continue;
            }

            if (Scope.EXCHANGE.equals(property.getScope())) {
                camelExchange.setProperty(property.getName(), property.getValue());
            } else {
                targetMessage.setHeader(property.getName(), property.getValue());
            }
        }

        for (String attachementName : switchyardExchange.getMessage().getAttachmentMap().keySet()) {
            targetMessage.addAttachment(attachementName, new DataHandler(switchyardExchange.getMessage().getAttachment(attachementName)));
        }

        ServiceOperation operation = switchyardExchange.getContract().getProviderOperation();
        targetMessage.setHeader(OPERATION_NAME, operation.getName());
        targetMessage.setHeader(FAULT_TYPE, operation.getFaultType());
        targetMessage.setHeader(SERVICE_NAME, switchyardExchange.getProvider().getName());
        camelExchange.setIn(targetMessage);

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

    private void sendResponse(org.apache.camel.Exchange camelExchange, final Exchange switchyardExchange) throws HandlerException {
        final org.apache.camel.Message camelMessage;
        if (camelExchange.hasOut()) {
            camelMessage = camelExchange.getOut();
        } else {
            camelMessage = camelExchange.getIn();
        }

        org.switchyard.Message message = switchyardExchange.createMessage();
        message.setContent(camelMessage.getBody());

        for (String property : camelExchange.getProperties().keySet()) {
            if (ContextPropertyUtil.isReservedProperty(property, Scope.EXCHANGE)) {
                continue;
            }
            message.getContext().setProperty(property, camelExchange.getProperty(property), Scope.EXCHANGE);
        }
        for (String header : camelMessage.getHeaders().keySet()) {
            if (ContextPropertyUtil.isReservedProperty(header, Scope.MESSAGE)) {
                continue;
            }
            message.getContext().setProperty(header, camelMessage.getHeader(header), Scope.MESSAGE);
        }

        for (String attachementName : camelMessage.getAttachmentNames()) {
            message.addAttachment(attachementName, new HandlerDataSource(camelMessage.getAttachment(attachementName)));
        }

        switchyardExchange.send(message);
    }

    private boolean isInOut(final Exchange exchange) {
        return exchange.getContract().getProviderOperation().getExchangePattern() == ExchangePattern.IN_OUT;
    }

    @Override
    public void handleFault(final Exchange exchange) {
        //TODO: Implement error handling.
    }

}
