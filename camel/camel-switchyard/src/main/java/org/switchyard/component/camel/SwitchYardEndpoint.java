/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *  *
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
package org.switchyard.component.camel;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.switchyard.component.camel.common.composer.BindingDataCreatorResolver;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelComposition;
import org.switchyard.component.common.composer.MessageComposer;

/**
 * A Camel Endpoint that is a simple ProcessorEndpoint.
 * 
 * @author Daniel Bevenius
 */
public class SwitchYardEndpoint extends DefaultEndpoint {
    
    /**
     * Producer property.
     */
    private String _namespace;

    /**
     * Producer property.
     */
    private String _operationName;

    /**
     * Producer property.
     */
    private MessageComposer<CamelBindingData> _messageComposer;

    /**
     * SwitchYard Consumer that handles events from SwitchYard and delegates
     * to the Camel processors.
     */
    private SwitchYardConsumer _consumer;

    /**
     * Sole constructor.
     * 
     * @param endpointUri The uri of the Camel endpoint. 
     * @param component The {@link SwitchYardComponent}.
     * @param namespace The service namespace that a Producer requires
     * @param operationName The operation name that a Producer requires
     */
    public SwitchYardEndpoint(final String endpointUri, final SwitchYardComponent component, final String namespace, final String operationName) {
        super(endpointUri, component);
        _namespace = namespace;
        _operationName = operationName;
    }

    /**
     * Sets the message composer this endpoint should pass along to producers and consumers.
     * @param messageComposer the message composer
     * @return this endpoint (useful for chaining)
     */
    public synchronized SwitchYardEndpoint setMessageComposer(MessageComposer<CamelBindingData> messageComposer) {
        _messageComposer = messageComposer;
        return this;
    }

    /**
     * Gets the message composer this endpoint should pass along to producers and consumers.
     * @return the message composer
     */
    public synchronized MessageComposer<CamelBindingData> getMessageComposer() {
        if (_messageComposer == null) {
            _messageComposer = CamelComposition.getMessageComposer();
        }
        return _messageComposer;
    }

    /**
     * Creates a event driven consumer as opposed to a polling consumer.
     * @param processor processor used by consumer
     * @return event-driven consumer
     * @throws Exception error creating consumer
     */
    @Override
    public Consumer createConsumer(final Processor processor) throws Exception {
        _consumer = new SwitchYardConsumer(this, processor, getMessageComposer());
        return _consumer;
    }

    /**
     * Gets the consumer for this endpoint.
     * 
     * @return {@link SwitchYardConsumer}
     */
    public SwitchYardConsumer getConsumer() {
        return _consumer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Producer createProducer() throws Exception {
        return new SwitchYardProducer(this, _namespace, _operationName, getMessageComposer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * Returns component binding data creator resolver.
     * 
     * @return Component binding data creator resolver.
     */
    public BindingDataCreatorResolver getBindingDataCreatorResolver() {
        return ((SwitchYardComponent) getComponent()).getBindingDataCreatorResolver();
    }

}
