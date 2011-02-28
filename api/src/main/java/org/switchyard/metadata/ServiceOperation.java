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

import org.switchyard.ExchangePattern;

/**
 * Representation of an operation on a ServiceInterface.
 * <p/>
 * Each operation has:
 * <ul>
 * <li>an exchange pattern (IN_OUT, IN_ONLY)
 * <li>a name
 * <li>an input message referenced type
 * <li>an (optional) output message type
 * <li>an (optional) fault message type
 * </ul>
 * <br>
 * The mapping of operation and message names is defined by the concrete 
 * implementation of ServiceInterface.  For example, the expected mapping of 
 * a Java interface would be Java method name to ServiceInterface operation name.
 */
public interface ServiceOperation extends InvocationContract {

    /**
     * The exchange pattern for the operation.
     * @return exchange pattern
     */
    ExchangePattern getExchangePattern();
    /**
     * The name of the operation.
     * @return operation name
     */
    String getName();
}
