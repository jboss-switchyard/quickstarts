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

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.v1.V1BaseTransformModel;
import org.switchyard.transform.config.model.JavaTransformModel;

/**
 * A version 1 JavaTransformModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1JavaTransformModel extends V1BaseTransformModel implements JavaTransformModel {

    /**
     * Constructs a new V1JavaTransformModel.
     */
    public V1JavaTransformModel() {
        super(new QName(TransformModel.DEFAULT_NAMESPACE, TransformModel.TRANSFORM + '.' + JAVA));
    }

    /**
     * Constructs a new V1JavaTransformModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1JavaTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClazz() {
        return getModelAttribute(CLASS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaTransformModel setClazz(String clazz) {
        setModelAttribute(CLASS, clazz);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBean() {
        return getModelAttribute(BEAN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaTransformModel setBean(String bean) {
        setModelAttribute(BEAN, bean);
        return this;
    }

}
