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
package org.switchyard.config.model.switchyard.v2;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.v2.V2PropertiesModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardMarshaller;

/**
 * Marshalls v2 switchyard Models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class V2SwitchYardMarshaller extends V1SwitchYardMarshaller {

    /**
     * Constructs a new V2SwitchYardMarshaller with the specified Descriptor.
     * @param desc the Descriptor
     */
    public V2SwitchYardMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (name.equals(PropertiesModel.PROPERTIES)) {
            return new V2PropertiesModel(config, desc);
        }
        return super.read(config);
    }

}
