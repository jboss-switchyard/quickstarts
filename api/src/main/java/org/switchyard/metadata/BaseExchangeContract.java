/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

import java.io.IOException;

import org.switchyard.io.Serialization.AccessType;
import org.switchyard.io.Serialization.CoverageType;
import org.switchyard.io.Serialization.Factory;
import org.switchyard.io.Serialization.Strategy;
import org.switchyard.metadata.BaseExchangeContract.BaseExchangeContractFactory;

/**
 * Base exchange contract.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Strategy(access=AccessType.FIELD, coverage=CoverageType.INCLUSIVE, factory=BaseExchangeContractFactory.class)
public class BaseExchangeContract implements ExchangeContract {

    private ServiceOperation _operation;
    private InvocationContract _invoker;
    
    private BaseExchangeContract() {}
    
    /**
     * Create a new BaseExchangeContract with the specified invocation contract.
     * @param operation contract for the service provider
     * @param invoker contract for the service consumer
     */
    public BaseExchangeContract(ServiceOperation operation, ServiceOperation invoker) {
        if (operation == null || invoker == null) {
            throw new IllegalArgumentException(
                    "BaseExchangeContract: operation and invoker parameters required");
        }
        _operation = operation;
        _invoker = invoker;
    }

    @Override
    public InvocationContract getInvokerInvocationMetaData() {
        return _invoker;
    }

    @Override
    public ServiceOperation getServiceOperation() {
        return _operation;
    }
    
    /**
     * The serialization factory for BaseExchangeContract.
     */
    public static final class BaseExchangeContractFactory implements Factory<BaseExchangeContract> {
        @Override
        public BaseExchangeContract create(Class<BaseExchangeContract> type) throws IOException {
            return new BaseExchangeContract();
        }
    }
}
