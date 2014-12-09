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

import org.switchyard.component.resteasy.config.model.ContextParamsModel;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.component.resteasy.config.model.v1.V1RESTEasyBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A model that holds the RESTEasy gateway configuration V2.
 */
public class V2RESTEasyBindingModel extends V1RESTEasyBindingModel {

    private ContextParamsModel _contextParamsConfig;

    /**
     * Constructor.
     * @param namespace namespace
     */
    public V2RESTEasyBindingModel(String namespace) {
        super(namespace);
    }

    /**
     * Create a RESTEasyBindingModel using configuration and descriptor.
     *
     * @param config the RESTEasyGateway configuration
     * @param desc the RESTEasyGateway descriptor
     */
    public V2RESTEasyBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public ContextParamsModel getContextParamsConfig() {
        if (_contextParamsConfig == null) {
            _contextParamsConfig = (ContextParamsModel) getFirstChildModel(ContextParamsModel.CONTEXT_PARAMS);
        }
        return _contextParamsConfig;
    }

    @Override
    public RESTEasyBindingModel setContextParamsConfig(ContextParamsModel contextParamsConfig) {
        setChildModel(contextParamsConfig);
        _contextParamsConfig = contextParamsConfig;
        return this;
    }

}
