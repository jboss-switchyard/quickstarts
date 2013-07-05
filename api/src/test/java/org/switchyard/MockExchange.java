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

package org.switchyard;

import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceOperation;

/**
 * Dumb implementation of Exchange meant for unit testing. 
 */
public class MockExchange implements Exchange {
    
    private ExchangeState _state;
    private ExchangePhase _phase;
    private ExchangeContract _contract;
    private Message _message;
    private Context _context;
    private ServiceReference _consumer;
    private Service _provider;
    
    public MockExchange() {
    }

    @Override
    public Message createMessage() {
        return _message;
    }

    @Override
    public Context getContext() {
        return _context;
    }

    @Override
    public Context getContext(Message message) {
        return message.getContext();
    }

    @Override
    public ExchangeContract getContract() {
        return _contract;
    }

    @Override
    public Message getMessage() {
        return _message;
    }

    @Override
    public ExchangePhase getPhase() {
        return _phase;
    }

    @Override
    public ExchangeState getState() {
        return _state;
    }

    @Override
    public void send(Message message) {
        // NOP
    }

    @Override
    public void sendFault(Message message) {
        // NOP
    }

    public MockExchange setPhase(ExchangePhase phase) {
        _phase = phase;
        return this;
    }
    
    public MockExchange setState(ExchangeState state) {
        _state = state;
        return this;
    }
    
    public MockExchange setMessage(Message message) {
        _message = message;
        return this;
    }
    
    public MockExchange setContext(Context context) {
        _context = context;
        return this;
    }
    
    public MockExchange setContract(ExchangeContract contract) {
        _contract = contract;
        return this;
    }

    @Override
    public ServiceReference getConsumer() {
        return _consumer;
    }

    @Override
    public Service getProvider() {
        return _provider;
    }
    
    @Override
    public MockExchange consumer(ServiceReference consumer, ServiceOperation operations) {
        _consumer = consumer;
        return this;
    }
    
    @Override
    public MockExchange provider(Service provider, ServiceOperation operations) {
        _provider = provider;
        return this;
    }

    @Override
    public ExchangeHandler getReplyHandler() {
        return null;
    }

    @Override
    public ExchangePattern getPattern() {
        ExchangePattern pattern = null;
        if (getContract() != null) {
            pattern = getContract().getConsumerOperation().getExchangePattern();
        }
        return pattern;
    }

}
