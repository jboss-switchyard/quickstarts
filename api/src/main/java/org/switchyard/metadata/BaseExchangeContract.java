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
package org.switchyard.metadata;

/**
 * Base implementation of ExchangeContract.
 */
public class BaseExchangeContract implements ExchangeContract {
    
    private ServiceOperation _consumer;
    private ServiceOperation _provider;

    /**
     * Create a new, empty BaseExchangeContract.
     */
    public BaseExchangeContract() {
        
    }
    
    /**
     * Create a new BaseExchangeContract with the specified consumer info.
     * @param consumer consumer contract
     */
    public BaseExchangeContract(ServiceOperation consumer) {
        _consumer = consumer;
    }
    
    /**
     * Create a new BaseExchangeContract with the specified consumer and provider info.
     * @param consumer consumer contract
     * @param provider provider contract
     */
    public BaseExchangeContract(ServiceOperation consumer, ServiceOperation provider) {
        _consumer = consumer;
        _provider = provider;
    }
    
    @Override
    public ServiceOperation getConsumerOperation() {
        return _consumer;
    }

    @Override
    public ServiceOperation getProviderOperation() {
        return _provider;
    }
    
    /**
     * Specify the consumer contract.
     * @param consumer consumer contract
     * @return this BaseExchangeContract instance
     */
    public BaseExchangeContract setConsumerOperation(ServiceOperation consumer) {
        _consumer = consumer;
        return this;
    }
    
    /**
     * Specify the provider contract.
     * @param provider provider contract
     * @return this BaseExchangeContract instance
     */
    public BaseExchangeContract setProviderOperation(ServiceOperation provider) {
        _provider = provider;
        return this;
    }

}
