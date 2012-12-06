/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
