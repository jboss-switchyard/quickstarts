/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.soap.config.model.v2;

import org.switchyard.component.soap.config.model.v1.V1SOAPMarshaller;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.v2.V2PropertiesModel;

/**
 * Marshaller for V2 SOAP Gateway configurations.
 */
public class V2SOAPMarshaller extends V1SOAPMarshaller {

    /**
     * Construct a SOAP Model Marshaller with help of a Descriptor.
     * 
     * @param desc the Descriptor 
     */
    public V2SOAPMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads a SOAP Model configuration.
     * 
     * @param config the configuration
     * @return the SOAP Binding Model 
     */
    @Override
    public Model read(Configuration config) {
        Descriptor desc = getDescriptor();
        String name = config.getName();
        if (name.equals(PropertiesModel.PROPERTIES)) {
            return new V2PropertiesModel(config, desc);
        }
        return super.read(config);
    }
}
