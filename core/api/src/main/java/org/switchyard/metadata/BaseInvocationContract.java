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

import javax.xml.namespace.QName;

/**
 * Base invocation contract.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BaseInvocationContract implements InvocationContract {

    private QName _inputType; // Undefined by default
    private QName _outputType; // Undefined by default
    private QName _faultType; // Undefined by default

    /**
     * Constructor.
     */
    public BaseInvocationContract() {
    }

    /**
     * Constructor.
     * @param inputType Input type associated with the invocation instance.
     * @param outputType Output type associated with the invocation instance.
     * @param faultType Fault type associated with the invocation instance.
     */
    public BaseInvocationContract(QName inputType, QName outputType, QName faultType) {
        _inputType = inputType;
        _outputType = outputType;
        _faultType = faultType;
    }

    /**
     * Set the input type associated with the invocation instance.
     * @param inputType Input type associated with the invocation instance.
     * @return {@link this} instance.
     */
    public BaseInvocationContract setInputType(QName inputType) {
        _inputType = inputType;
        return this;
    }

    @Override
    public QName getInputType() {
        return _inputType;
    }

    /**
     * Set the output type associated with the invocation instance.
     * @param outputType Output type associated with the invocation instance.
     * @return {@link this} instance.
     */
    public BaseInvocationContract setOutputType(QName outputType) {
        _outputType = outputType;
        return this;
    }

    @Override
    public QName getOutputType() {
        return _outputType;
    }

    /**
     * Set the fault type associated with the invocation instance.
     * @param faultType Fault type associated with the invocation instance.
     * @return {@link this} instance.
     */
    public BaseInvocationContract setFaultType(QName faultType) {
        _faultType = faultType;
        return this;
    }

    @Override
    public QName getFaultType() {
        return _faultType;
    }
}
