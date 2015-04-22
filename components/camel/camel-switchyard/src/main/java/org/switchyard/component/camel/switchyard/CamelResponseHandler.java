/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.camel.switchyard;

import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.ServiceReference;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.common.composer.MessageComposer;

/**
 * A CamelResponseHandler is responsible for passing back result data from Apache Camel to
 * SwitchYard.
 * 
 * By given access to the CamelExchange this ExchangeHandler can extract the SwitchYard payload
 * and set in into the Camel Exchange.
 * 
 * @author Daniel Bevenius
 */
public class CamelResponseHandler implements ExchangeHandler {

    private final org.apache.camel.Exchange _camelExchange;
    private final MessageComposer<CamelBindingData> _messageComposer;

    /**
     * Sole constructor.
     * 
     * @param camelExchange The Camel {@link org.apache.camel.Exchange}
     * @param reference The SwitchYard ServiceReference.
     * @param messageComposer the MessageComposer to use
     */
    public CamelResponseHandler(final org.apache.camel.Exchange camelExchange, final ServiceReference reference, final MessageComposer<CamelBindingData> messageComposer) {
        if (camelExchange ==  null) {
            throw SwitchYardCamelComponentMessages.MESSAGES.camelExchangeArgumentMustNotBeNull();
        }
        if (reference == null) {
            throw SwitchYardCamelComponentMessages.MESSAGES.referenceArgumentMustNotBeNull();
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
            compose(switchYardExchange);
        } catch (Exception e) {
            throw new HandlerException(e);
        }
    }

    private Message getCamelMessage() {
        return isInOnly() ? _camelExchange.getIn() : _camelExchange.getOut();
    }

    private boolean isInOnly() {
        return _camelExchange.getPattern().equals(ExchangePattern.InOnly);
    }

    @Override
    public void handleFault(final Exchange exchange) {
        final Object content = exchange.getMessage().getContent();

        if (content instanceof Throwable) {
            _camelExchange.setException((Throwable) content);
            return;
        }

        try {
            Message camelMsg = compose(exchange);
            camelMsg.setFault(true);
        } catch (Exception e) {
            _camelExchange.setException(e);
        }
    }

    private Message compose(Exchange exchange) throws Exception {
        Message camelMsg;
        if (_messageComposer != null) {
            camelMsg = getCamelMessage();
            _messageComposer.decompose(exchange, new CamelBindingData(camelMsg));
        } else {
            camelMsg = ExchangeMapper.mapSwitchYardToCamel(exchange, _camelExchange);
            if (isInOnly()) {
                _camelExchange.setIn(camelMsg);
            } else {
                _camelExchange.setOut(camelMsg);
            }
        }
        return camelMsg;
    }
}
