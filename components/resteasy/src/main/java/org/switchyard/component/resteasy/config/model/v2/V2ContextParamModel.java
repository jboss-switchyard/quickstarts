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

import javax.xml.namespace.QName;

import org.switchyard.component.resteasy.config.model.ContextParamModel;
import org.switchyard.component.resteasy.config.model.ContextParamsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 2 ContextParamModel.
 */
public class V2ContextParamModel extends BaseModel implements ContextParamModel {

    private static final String NAME = "name";
    private static final String VALUE = "value";

    /**
     * Creates a new ContextParamModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V2ContextParamModel(String namespace) {
        super(new QName(namespace, CONTEXT_PARAM));
    }

    /**
     * Creates a new ContextParamModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V2ContextParamModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextParamsModel getContextParamsModel() {
        return (ContextParamsModel) getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getModelAttribute(NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextParamModel setName(String name) {
        setModelAttribute(NAME, name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return getModelAttribute(VALUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextParamModel setValue(String value) {
        setModelAttribute(VALUE, value);
        return this;
    }

}
