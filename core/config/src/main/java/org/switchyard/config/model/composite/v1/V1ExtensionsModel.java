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

package org.switchyard.config.model.composite.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.ExtensionsModel;
import org.switchyard.config.model.composite.SCANamespace;
import org.switchyard.config.model.switchyard.ThrottlingModel;

/**
 * A version 1 ExtensionsModel.
 */
public class V1ExtensionsModel extends BaseModel implements ExtensionsModel {

    private ThrottlingModel _throttling;

    /**
     * Constructs a new V1ExtensionsModel.
     */
    public V1ExtensionsModel() {
        super(new QName(SCANamespace.DEFAULT.uri(), V1ExtensionsModel.EXTENSIONS));
    }

    /**
     * Constructs a new V1ExtensionsModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ExtensionsModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public ThrottlingModel getThrottling() {
        if (_throttling == null) {
            _throttling = (ThrottlingModel) getFirstChildModel(ThrottlingModel.THROTTLING);
        }
        return _throttling;
    }

    @Override
    public ExtensionsModel setThrottling(ThrottlingModel throttling) {
        setChildModel(throttling);
        _throttling = throttling;
        return this;
    }

}
