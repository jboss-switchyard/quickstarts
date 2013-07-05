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
