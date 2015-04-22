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
import org.switchyard.transform.config.model.JAXBTransformModel;
import org.switchyard.transform.internal.TransformerFactoryClass;
import org.switchyard.transform.jaxb.internal.JAXBTransformerFactory;

/**
 * A version 1 JAXBTransformModel.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@TransformerFactoryClass(JAXBTransformerFactory.class)
public class V1JAXBTransformModel extends V1BaseTransformModel implements JAXBTransformModel {

    /**
     * Constructs a new V1JAXBTransformModel.
     * @param namespace namespace
     */
    public V1JAXBTransformModel(String namespace) {
        super(new QName(namespace, TransformModel.TRANSFORM + '.' + JAXB));
    }

    /**
     * Constructs a new V1SmooksTransformModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1JAXBTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getContextPath() {
        return getModelAttribute(CONTEXT_PATH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBTransformModel setContextPath(String contextPath) {
        setModelAttribute(CONTEXT_PATH, contextPath);
        return this;
    }
}
