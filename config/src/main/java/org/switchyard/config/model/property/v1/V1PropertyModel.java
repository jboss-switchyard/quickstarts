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
package org.switchyard.config.model.property.v1;

import static org.switchyard.config.model.switchyard.SwitchYardModel.DEFAULT_NAMESPACE;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.PropertyModel;

/**
 * Implementation of PropertyModel : v1.
 */
public class V1PropertyModel extends BaseModel implements PropertyModel {

    /**
     * Creates a new PropertyModel in the default namespace.
     */
    public V1PropertyModel() {
        this(DEFAULT_NAMESPACE);
    }

    /**
     * Creates a new PropertyModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1PropertyModel(String namespace) {
        super(new QName(namespace, PROPERTY));
    }

    /**
     * Creates a new PropertyModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1PropertyModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesModel getProperties() {
        return (PropertiesModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getModelAttribute("name");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyModel setName(String name) {
        setModelAttribute("name", name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return getModelAttribute("value");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyModel setValue(String value) {
        setModelAttribute("value", value);
        return this;
    }

}
