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

package org.switchyard.config.model.switchyard;

import javax.xml.namespace.QName;

import org.switchyard.config.model.composite.InterfaceModel;

/**
 * An "esb" InterfaceModel.
 */
public interface EsbInterfaceModel extends InterfaceModel {

    /**
     * The "esb" implementation type.
     */
    static final String ESB = "esb";
    
    /**
     * The "inputType" parameter name.
     */
    static final String INPUT_TYPE = "inputType";

    /**
     * The "outputType" parameter name.
     */
    static final String OUTPUT_TYPE = "outputType";
    
    /**
     * The "faultType" parameter name.
     */
    static final String FAULT_TYPE = "faultType";
    
    /**
     * Returns the input type for the interface.
     * @return input type
     */
    QName getInputType();
    
    /**
     * Returns the output type for the interface.
     * @return input type
     */
    QName getOutputType();
    
    /**
     * Returns the fault type for the interface.
     * @return input type
     */
    QName getFaultType();
    
    /**
     * Sets the input type for the interface.
     * @param input input type
     * @return reference to this InterfaceModel
     */
    EsbInterfaceModel setInputType(QName input);
    
    /**
     * Sets the output type for the interface.
     * @param output output type
     * @return reference to this InterfaceModel
     */
    EsbInterfaceModel setOutputType(QName output);
    
    /**
     * Sets the fault type for the interface.
     * @param fault fault type
     * @return reference to this InterfaceModel
     */
    EsbInterfaceModel setFaultType(QName fault);

}
