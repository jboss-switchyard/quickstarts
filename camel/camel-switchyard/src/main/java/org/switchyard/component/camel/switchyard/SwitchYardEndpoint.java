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

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;
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
     * @param operationName The operation name that a Producer requires
     */
    public SwitchYardEndpoint(final String endpointUri, final SwitchYardComponent component, final String operationName) {
        super(endpointUri, component);
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
        _consumer = new SwitchYardConsumer(this, processor);
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
        return new SwitchYardProducer(this, _operationName, getMessageComposer());
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

    public ServiceDomain getServiceDomain() {
        return ((SwitchYardCamelContext) getCamelContext()).getServiceDomain();
    }
}
