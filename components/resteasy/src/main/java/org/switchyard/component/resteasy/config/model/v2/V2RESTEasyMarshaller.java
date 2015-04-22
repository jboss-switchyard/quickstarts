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
package org.switchyard.component.resteasy.config.model.v2;

import org.switchyard.component.resteasy.config.model.ContextParamModel;
import org.switchyard.component.resteasy.config.model.ContextParamsModel;
import org.switchyard.component.resteasy.config.model.v1.V1RESTEasyMarshaller;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;

/**
 * Marshaller (v2) for RESTEasy Gateway configurations.
 */
public class V2RESTEasyMarshaller extends V1RESTEasyMarshaller {

    /**
     * Construct a RESTEasy Model Marshaller (v2) with help of a Descriptor.
     *
     * @param desc the Descriptor
     */
    public V2RESTEasyMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads a RESTEasy Model configuration.
     *
     * @param config the configuration
     * @return the RESTEasy Binding Model
     */
    @Override
    public Model read(Configuration config) {
        Descriptor desc = getDescriptor();
        String name = config.getName();
        if (name.startsWith(BindingModel.BINDING)) {
            return new V2RESTEasyBindingModel(config, desc);
        } else if (name.equals(ContextParamsModel.CONTEXT_PARAMS)) {
            return new V2ContextParamsModel(config, desc);
        } else if (name.equals(ContextParamModel.CONTEXT_PARAM)) {
            return new V2ContextParamModel(config, desc);
        }
        return super.read(config);
    }

}
