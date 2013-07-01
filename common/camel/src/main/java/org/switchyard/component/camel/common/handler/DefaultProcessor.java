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
package org.switchyard.component.camel.common.handler;

import org.apache.camel.Processor;
import org.switchyard.Exchange;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.common.composer.MessageComposer;

/**
 * A default Camel Processor.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class DefaultProcessor implements Processor {

    private final MessageComposer<CamelBindingData> _messageComposer;
    private Exchange _exchange;

    /**
     * Create a DefaultProcessor that handles all generic outbound routes.
     *
     * @param composer the message composer to be used
     * @param exchange the switchayrd exchange
     */
    public DefaultProcessor(MessageComposer<CamelBindingData> composer, Exchange exchange) {
        _messageComposer = composer;
        _exchange = exchange;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(org.apache.camel.Exchange camelExchange) throws Exception {
        _messageComposer.decompose(_exchange, new CamelBindingData(camelExchange.getIn()));
    }

    /**
     * Returns the SwitchYard exchange associated with this processor.
     *
     * @return the SwicthYard exchange
     */
    public Exchange getExchange() {
        return _exchange;
    }

    /**
     * Returns the composer associated with this processor.
     *
     * @return the message composer
     */
    public MessageComposer<CamelBindingData> getComposer() {
        return _messageComposer;
    }
}
