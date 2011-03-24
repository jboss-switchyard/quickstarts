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

import javax.xml.namespace.QName;

import org.switchyard.io.Serialization.CoverageType;
import org.switchyard.io.Serialization.Factory;
import org.switchyard.io.Serialization.Strategy;
import org.switchyard.metadata.InOutContract.InOutContractFactory;

/**
 * Default/opaque {@link org.switchyard.ExchangePattern#IN_OUT} exchange
 * contract.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Strategy(coverage=CoverageType.EXCLUSIVE, factory=InOutContractFactory.class)
class InOutContract implements ExchangeContract {

    /**
     * The serialization factory for InOutContract.
     */
    public static final class InOutContractFactory implements Factory<InOutContract> {
        /**
         * {@inheritDoc}
         */
        @Override
        public InOutContract create(Class<InOutContract> type) throws IOException {
            return (InOutContract)ExchangeContract.IN_OUT;
        }
    }

    /**
     * Default invoker invocation metadata.
     */
    private static final InvocationContract INVOCATION_METADATA = new InvocationContract() {
            @Override
            public QName getInputType() {
                return null; // Undefined!!
            }
            @Override
            public QName getOutputType() {
                return null; // Undefined!!
            }
            @Override
            public QName getFaultType() {
                return null; // Undefined!!
            }
        };
    /**
     * Default operation.
     */
    private static final InOutOperation SERVICE_OP = new InOutOperation(ServiceInterface.DEFAULT_OPERATION);

    @Override
    public InvocationContract getInvokerInvocationMetaData() {
        return INVOCATION_METADATA;
    }

    @Override
    public ServiceOperation getServiceOperation() {
        return SERVICE_OP;
    }
}
