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
package org.switchyard.admin.base;

import javax.xml.namespace.QName;

import org.switchyard.admin.ServiceOperation;

/**
 * Implementation of service operation.
 */
public class BaseServiceOperation extends BaseMessageMetricsAware implements ServiceOperation {

    private String _name;
    private String _exchangePattern;
    private QName _inputType;
    private QName _outputType;
    private QName _faultType;

    /**
     * Creates new service operation.
     * @param name Operation name.
     * @param exchangePattern Operation exchange pattern.
     * @param inputType Input type for operation.
     * @param outputType Output type for operation.
     * @param faultType Fault type returned by operation in case of failures.
     */
    public BaseServiceOperation(String name, String exchangePattern, QName inputType, QName outputType, QName faultType) {
        this._name = name;
        this._exchangePattern = exchangePattern;
        this._inputType = inputType;
        this._outputType = outputType;
        this._faultType = faultType;
    }

    @Override
    public String getExchangePattern() {
        return _exchangePattern;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public QName getInputType() {
        return _inputType;
    }

    @Override
    public QName getOutputType() {
        return _outputType;
    }

    @Override
    public QName getFaultType() {
        return _faultType;
    }

}
