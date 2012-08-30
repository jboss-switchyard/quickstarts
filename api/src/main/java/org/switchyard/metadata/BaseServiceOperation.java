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
        return _name + " : " + _pattern + " : [" + getInputType() + ", " + getOutputType() + ", " + getFaultType() + "]";
    }
    
}
