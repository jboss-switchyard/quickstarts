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

import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.Service;
import org.switchyard.spi.Dispatcher;

/**
 * Creates a Dispatcher instance for handling message exchange for a SwitchYard
 * service.
 */
public class ExchangeDispatcher implements Dispatcher {

    /**
     * Property used to store a reference to the SY exchange in a Camel exchange.
     */
    public static final String SY_EXCHANGE = "SwitchYardExchange";
    
    private Service _service;
    private ProducerTemplate _producer;

    /**
     * Create a new Dispatcher instance.
     * @param service dispatch for this service
     * @param producer Camel producer template used to consume a service
     */
    public ExchangeDispatcher(Service service, ProducerTemplate producer) {
        _service = service;
        _producer = producer;
    }
    
    @Override
    public Service getService() {
        return _service;
    }

    @Override
    public void dispatch(final Exchange exchange) {
        if (exchange.getPhase().equals(ExchangePhase.IN)) {
            _producer.send("direct:" + exchange.getServiceName(), 
                    new Processor() {
                        public void process(org.apache.camel.Exchange ex) {
                            ex.setProperty(SY_EXCHANGE, exchange);
                            ex.getIn().setBody(exchange.getMessage().getContent());
                        }
                    });
        }
    }
}
