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

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * A Camel Endpoint that is a simple {@link ProcessorEndpoint}.
 * 
 * @author Daniel Bevenius
 *
 */
public class SwitchyardEndpoint extends DefaultEndpoint {
    /**
     * Producer property.
     */
    private String _operationName;
    
    /**
     * SwitchYard Consumer that handles events from SwitchYard and delegates
     * to the Camel processors.
     */
    private SwitchYardConsumer _consumer;
    
    /**
     * Sole constructor.
     * 
     * @param endpointUri The uri of the Camel endpoint. 
     * @param component The {@link SwitchyardComponent}.
     * @param operationName The operation name that a Producer requires
     */
    public SwitchyardEndpoint(final String endpointUri, final SwitchyardComponent component, final String operationName) {
        super(endpointUri, component);
        _operationName = operationName;
    }

    /**
     * Creates a event driven consumer as opposed to a polling consumer.
     * @param processor processor used by consumer
     * @return event-driven consumer
     * @throws Exception error creating consumer
     */
    @Override
    public Consumer createConsumer(final Processor processor) throws Exception {
        if (_operationName == null) {
            _consumer = new SwitchYardConsumer(this, processor);
            return _consumer;
        }
        return null;
    }
    
    /**
     * Gets the consumer for this endpoint.
     * 
     * @return {@link DefaultConsumer}
     */
    public SwitchYardConsumer getConsumer() {
        return _consumer;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new SwitchYardProducer(this, _operationName);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
    
}
