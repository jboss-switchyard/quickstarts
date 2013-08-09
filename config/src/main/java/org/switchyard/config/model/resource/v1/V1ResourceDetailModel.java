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
package org.switchyard.config.model.resource.v1;

import static org.switchyard.config.model.switchyard.SwitchYardModel.DEFAULT_NAMESPACE;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.resource.ResourceDetailModel;

/**
 * The 1st version ResourceDetailModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2011 Red Hat Inc.
 */
public class V1ResourceDetailModel extends BaseModel implements ResourceDetailModel {

    /**
     * Creates a new ResourceDetailModel in the default namespace.
     */
    public V1ResourceDetailModel() {
        this(DEFAULT_NAMESPACE);
    }

    /**
     * Creates a new ResourceDetailModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1ResourceDetailModel(String namespace) {
        super(new QName(namespace, RESOURCE_DETAIL));
    }

    /**
     * Creates a new ResourceDetailModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ResourceDetailModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInputType() {
        return getModelAttribute("inputType");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceDetailModel setInputType(String inputType) {
        setModelAttribute("inputType", inputType);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWorksheetName() {
        return getModelAttribute("worksheetName");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceDetailModel setWorksheetName(String worksheetName) {
        setModelAttribute("worksheetName", worksheetName);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUsingExternalTypes() {
        return Boolean.valueOf(getModelAttribute("usingExternalTypes")).booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceDetailModel setUsingExternalTypes(boolean usingExternalTypes) {
        setModelAttribute("usingExternalTypes", String.valueOf(usingExternalTypes));
        return this;
    }

}
