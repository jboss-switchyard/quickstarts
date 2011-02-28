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

/**
 * Exchange Contract.
 * <p/>
 * Two-way Exchange contract, detailing the requirements of both the invoker (in terms of
 * accepted response/fault types) and target {@link ServiceOperation} being invoked.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface ExchangeContract {

    /**
     * Default {@link org.switchyard.ExchangePattern#IN_ONLY} exchange contract.
     */
    ExchangeContract IN_ONLY = new InOnlyContract();
    /**
     * Default {@link org.switchyard.ExchangePattern#IN_OUT} exchange contract.
     */
    ExchangeContract IN_OUT = new InOutContract();

    /**
     * Get the invocation metadata associated with the exchange invoker.
     * <p/>
     * This represents the data input into the exchange by the invoker/consumer,
     * before it sends the message to the target service operation.  Also represents
     * the data types it requires back in the exchange, including fault types if a fault
     * occurs.
     * @return The exchange invoker's invocation metadata.
     */
    InvocationContract getInvokerInvocationMetaData();

    /**
     * Get the target service operation being invoked.
     * @return The target service operation being invoked.
     */
    ServiceOperation getServiceOperation();
}
