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

import static org.switchyard.component.soap.config.model.SOAPBindingModel.DEFAULT_NAMESPACE;

import org.switchyard.component.soap.config.model.MtomModel;
import org.switchyard.component.soap.config.model.SOAPNameValueModel.SOAPName;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version MtomModel.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class V1MtomModel extends BaseModel implements MtomModel {

    private static final String ENABLED = "enabled";
    private static final String THRESHOLD = "threshold";
    private static final String XOP_EXPAND = "xopExpand";

    private Boolean _enabled = false;

    /**
     * Creates a new MtomModel.
     */
    public V1MtomModel() {
        super(SOAPName.mtom.name(), DEFAULT_NAMESPACE);
    }

    /**
     * Creates a new MtomModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1MtomModel(Configuration config, Descriptor desc) {
        super(config, desc);
        _enabled = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isEnabled() {
        String enabled = getModelAttribute(ENABLED);
        if (enabled != null) {
            return Boolean.valueOf(enabled);
        }
        return _enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MtomModel setEnabled(Boolean enabled) {
        setModelAttribute(ENABLED, String.valueOf(enabled));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getThreshold() {
        String threshold = getModelAttribute(THRESHOLD);
        if (threshold != null) {
            return Integer.valueOf(threshold);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MtomModel setThreshold(Integer threshold) {
        setModelAttribute(THRESHOLD, String.valueOf(threshold));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isXopExpand() {
        String expand = getModelAttribute(XOP_EXPAND);
        if (expand != null) {
            return Boolean.valueOf(expand);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MtomModel setXopExpand(Boolean expand) {
        setModelAttribute(XOP_EXPAND, String.valueOf(expand));
        return this;
    }

}
