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
import org.switchyard.transform.config.model.SmooksTransformModel;
import org.switchyard.transform.config.model.SmooksTransformType;
import org.switchyard.transform.internal.TransformerFactoryClass;
import org.switchyard.transform.smooks.internal.SmooksTransformFactory;

/**
 * A version 1 SmooksTransformModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@TransformerFactoryClass(SmooksTransformFactory.class)
public class V1SmooksTransformModel extends V1BaseTransformModel implements SmooksTransformModel {

    /**
     * Constructs a new V1SmooksTransformModel.
     */
    public V1SmooksTransformModel() {
        super(new QName(TransformModel.DEFAULT_NAMESPACE, TransformModel.TRANSFORM + '.' + SMOOKS));
    }

    /**
     * Constructs a new V1SmooksTransformModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1SmooksTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTransformType() {
        return getModelAttribute(TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V1SmooksTransformModel setTransformType(String type) {
        setModelAttribute(TYPE, type);
        return this;
    }

    /**
     * Set the Smooks Transformation type.
     * @param type The transformation type.
     * @return <code>this</code> TransformModel instance.
     */
    public V1SmooksTransformModel setTransformType(SmooksTransformType type) {
        setModelAttribute(TYPE, type.toString());
        return this;
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
    public V1SmooksTransformModel setConfig(String config) {
        setModelAttribute(CONFIG, config);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReportPath() {
        return getModelAttribute(REPORT_PATH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V1SmooksTransformModel setReportPath(String reportPath) {
        setModelAttribute(REPORT_PATH, reportPath);
        return this;
    }

}
