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
package org.switchyard.component.soap.config.model.v1;

import org.switchyard.component.soap.config.model.SOAPNameValueModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version SOAPNameValueModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class V1SOAPNameValueModel extends BaseModel implements SOAPNameValueModel {

    /**
     * Creates a new V1SOAPNameValueModel.
     * @param namespace namespace
     * @param name the SOAPName
     */
    public V1SOAPNameValueModel(String namespace, SOAPName name) {
        super(namespace, name.name());
    }

    /**
     * Creates a new V1SOAPNameValueModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1SOAPNameValueModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPName getName() {
        return SOAPName.valueOf(getModelConfiguration().getName());
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
    public SOAPNameValueModel setValue(String value) {
        setModelValue(value);
        return this;
    }

}
