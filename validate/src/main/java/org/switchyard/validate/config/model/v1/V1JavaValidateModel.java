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

package org.switchyard.validate.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.v1.V1BaseValidateModel;
import org.switchyard.validate.config.model.JavaValidateModel;

/**
 * A version 1 JavaValidateModel.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class V1JavaValidateModel extends V1BaseValidateModel implements JavaValidateModel {

    /**
     * Constructs a new V1JavaValidateModel.
     * @param namespace namespace
     */
    public V1JavaValidateModel(String namespace) {
        super(new QName(namespace, ValidateModel.VALIDATE + '.' + JAVA));
    }

    /**
     * Constructs a new V1JavaValidateModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1JavaValidateModel(Configuration config, Descriptor desc) {
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
    public JavaValidateModel setClazz(String clazz) {
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
    public JavaValidateModel setBean(String bean) {
        setModelAttribute(BEAN, bean);
        return this;
    }

}
