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

package org.switchyard.config.model.switchyard.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1InterfaceModel;
import org.switchyard.config.model.switchyard.EsbInterfaceModel;

/**
 * An "esb" implementation of InterfaceModel.
 */
public class V1EsbInterfaceModel extends V1InterfaceModel implements EsbInterfaceModel {

    /**
     * Default constructor for application use.
     */
    public V1EsbInterfaceModel() {
        super(ESB);
    }

    /**
     * Constructor for Marshaller use.
     *
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1EsbInterfaceModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public QName getInputType() {
        return getQName(getModelAttribute(INPUT_TYPE));
    }

    @Override
    public QName getOutputType() {
        return getQName(getModelAttribute(OUTPUT_TYPE));
    }

    @Override
    public QName getFaultType() {
        return getQName(getModelAttribute(FAULT_TYPE));
    }

    @Override
    public EsbInterfaceModel setInputType(QName input) {
        setModelAttribute(INPUT_TYPE, input.toString());
        return this;
    }

    @Override
    public EsbInterfaceModel setOutputType(QName output) {
        setModelAttribute(OUTPUT_TYPE, output.toString());
        return this;
    }

    @Override
    public EsbInterfaceModel setFaultType(QName fault) {
        setModelAttribute(FAULT_TYPE, fault.toString());
        return this;
    }
    
    private QName getQName(String name) {
        return name == null ? null : QName.valueOf(name);
    }

}
