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

package org.switchyard.transform.config.model.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.v1.V1BaseTransformModel;
import org.switchyard.transform.config.model.Java2XmlTransformModel;

import javax.xml.namespace.QName;

/**
 * A version 1 Java2XmlTransformModel.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class V1Java2XmlTransformModel extends V1BaseTransformModel implements Java2XmlTransformModel {

    /**
     * Constructs a new V1Java2XmlTransformModel.
     */
    public V1Java2XmlTransformModel() {
        super(new QName(TransformModel.DEFAULT_NAMESPACE, TransformModel.TRANSFORM + '.' + JAVA2XML));
    }

    /**
     * Constructs a new V1Java2XmlTransformModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1Java2XmlTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfig() {
        return getModelAttribute(CONFIG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V1Java2XmlTransformModel setConfig(String config) {
        setModelAttribute(CONFIG, config);
        return this;
    }

}
