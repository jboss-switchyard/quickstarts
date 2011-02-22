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
