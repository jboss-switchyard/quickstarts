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

package org.switchyard.metadata;

import javax.xml.namespace.QName;

import org.switchyard.ExchangePattern;

/**
 * Base invocation contract.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BaseServiceOperation implements ServiceOperation {

    private QName           _inputType;
    private QName           _outputType;
    private QName           _faultType;
    private String          _name;
    private ExchangePattern _pattern;
    
    protected BaseServiceOperation(ExchangePattern pattern) {
        _pattern = pattern;
    }

    /**
     * Constructor.
     * @param pattern the exchange pattern
     * @param name the operation name
     * @param inputType Input type associated with the invocation instance.
     * @param outputType Output type associated with the invocation instance.
     * @param faultType Fault type associated with the invocation instance.
     */
    public BaseServiceOperation(
            ExchangePattern pattern,
            String name,
            QName inputType, 
            QName outputType, 
            QName faultType) {
        _pattern = pattern;
        _name = name;
        _inputType = inputType;
        _outputType = outputType;
        _faultType = faultType;
    }

    /**
     * Set the input type associated with the invocation instance.
     * @param inputType Input type associated with the invocation instance.
     * @return {@link this} instance.
     */
    public BaseServiceOperation setInputType(QName inputType) {
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
    public BaseServiceOperation setOutputType(QName outputType) {
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
    public BaseServiceOperation setFaultType(QName faultType) {
        _faultType = faultType;
        return this;
    }

    @Override
    public QName getFaultType() {
        return _faultType;
    }

    @Override
    public ExchangePattern getExchangePattern() {
        return _pattern;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return String.format("%s : %s  : [%s, %s, %s]", _name, _pattern, getInputType(), 
                getOutputType(), getFaultType());
    }
    
    // Protected methods provide deserialization access for fields
    
    protected void setPattern(ExchangePattern pattern) {
        _pattern = pattern;
    }
    
    protected void setName(String name) {
        _name = name;
    }
    
    protected void setExchangePattern(ExchangePattern pattern) {
        _pattern = pattern;
    }
}
