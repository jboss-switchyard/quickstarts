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
