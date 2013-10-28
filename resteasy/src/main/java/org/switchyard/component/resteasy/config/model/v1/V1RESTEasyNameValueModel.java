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
package org.switchyard.component.resteasy.config.model.v1;

import org.switchyard.component.resteasy.config.model.RESTEasyNameValueModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version RESTEasyNameValueModel.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class V1RESTEasyNameValueModel extends BaseModel implements RESTEasyNameValueModel {

    /**
     * Creates a new V1RESTEasyNameValueModel.
     * @param namespace namespace
     * @param name the RESTEasyName
     */
    public V1RESTEasyNameValueModel(String namespace, RESTEasyName name) {
        super(namespace, name.name());
    }

    /**
     * Creates a new V1RESTEasyNameValueModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1RESTEasyNameValueModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RESTEasyName getName() {
        return RESTEasyName.valueOf(getModelConfiguration().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return getModelValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RESTEasyNameValueModel setValue(String value) {
        setModelValue(value);
        return this;
    }

}
