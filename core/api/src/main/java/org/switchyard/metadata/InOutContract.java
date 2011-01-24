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

package org.switchyard.metadata;

/**
 * Default/opaque {@link org.switchyard.ExchangePattern#IN_OUT} exchange
 * contract.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
class InOutContract implements ExchangeContract {

    /**
     * Default invoker invocation metadata.
     */
    private static final InvocationContract INVOCATION_METADATA = new InvocationContract() {
            @Override
            public String getInputType() {
                return null; // Undefined!!
            }
            @Override
            public String getOutputType() {
                return null; // Undefined!!
            }
            @Override
            public String getFaultType() {
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
