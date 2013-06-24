/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.bus.camel;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.model.ModelCamelContext;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangePhase;
import org.switchyard.ServiceReference;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.metadata.qos.Throttling;
import org.switchyard.spi.Dispatcher;

/**
 * Creates a Dispatcher instance for handling message exchange for a SwitchYard
 * service.
 */
public class ExchangeDispatcher implements Dispatcher {

    private final ModelCamelContext _context;
    private ServiceReference _reference;
    private ProducerTemplate _producer;

    /**
     * Create a new Dispatcher instance.
     * @param context Camel context instance
     * @param reference dispatch for this reference
     */
    public ExchangeDispatcher(ModelCamelContext context, ServiceReference reference) {
        _context = context;
        _reference = reference;
        _producer = context.createProducerTemplate();
    }

    @Override
    public ServiceReference getServiceReference() {
        return _reference;
    }

    @Override
    public Exchange createExchange(ExchangeHandler replyHandler, ExchangePattern pattern) {
        DefaultExchange exchange = new DefaultExchange(_context, translate(pattern));
        exchange.setIn(new CamelMessage(exchange));
        return new CamelExchange(this, exchange, replyHandler);
    }

    private org.apache.camel.ExchangePattern translate(ExchangePattern pattern) {
        return ExchangePattern.IN_OUT == pattern ? org.apache.camel.ExchangePattern.InOut : org.apache.camel.ExchangePattern.InOnly;
    }

    @Override
    public void dispatch(final Exchange exchange) {
        // We can only send Camel exchanges through the camel bus
        if (!CamelExchange.class.isInstance(exchange)) {
            throw new IllegalArgumentException("Camel Bus accepts only CamelExchanges");
        }
        CamelExchange camelEx = (CamelExchange)exchange;
        
        // For camel exchanges, the only phase we care about is IN.  The dispatch method can also
        // be called on the OUT path, but that should be handled by the IN_OUT filter in the Camel
        // bus route.
        if (!exchange.getPhase().equals(ExchangePhase.IN)) {
            return;
        }

        Throttling throttling = _reference.getServiceMetadata().getThrottling();
        if (throttling != null && throttling.getMaxRequests() > 0) {
            exchange.getMessage().getContext().setProperty(
                    Throttling.MAX_REQUESTS, throttling.getMaxRequests())
                    .addLabels(BehaviorLabel.TRANSIENT.label());
        }
        _producer.send("direct:" + exchange.getConsumer().getName(), camelEx.getExchange());
        
    }

}
