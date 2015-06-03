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

package org.switchyard.deploy.components.config.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;

public class V1MockModelMarshaller extends BaseMarshaller {
    
    /**
     * Create a new MockModel marshaller.
     * @param desc The switchyard descriptor.
     */
    public V1MockModelMarshaller(final Descriptor desc) {
        super(desc);
    }
    
    @Override
    public Model read(final Configuration config) {
        Model model = null;
        if (config.getName().startsWith(BindingModel.BINDING)) {
            model = new V1MockBindingModel(config, getDescriptor());
        } else if (config.getName().startsWith(ComponentImplementationModel.IMPLEMENTATION)) {
            model = new V1MockImplementationModel(config, getDescriptor());
        }
        return model;
    }
}
