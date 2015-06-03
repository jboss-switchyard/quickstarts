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
package org.switchyard.component.http.config.model.v1;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.http.config.model.HttpNameValueModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version HttpNameValueModel.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class V1HttpNameValueModel extends BaseModel implements HttpNameValueModel {

    /**
     * Creates a new V1HttpNameValueModel.
     * @param namespace namespace
     * @param name the HttpName
     */
    public V1HttpNameValueModel(String namespace, HttpName name) {
        super(XMLHelper.createQName(namespace, name.name()));
    }

    /**
     * Creates a new V1HttpNameValueModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1HttpNameValueModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpName getName() {
        return HttpName.valueOf(getModelConfiguration().getName());
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
    public HttpNameValueModel setValue(String value) {
        setModelValue(value);
        return this;
    }

}
