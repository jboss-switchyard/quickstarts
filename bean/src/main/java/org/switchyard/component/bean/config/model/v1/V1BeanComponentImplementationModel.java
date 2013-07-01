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
package org.switchyard.component.bean.config.model.v1;

import org.switchyard.component.bean.config.model.BeanComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1ComponentImplementationModel;

/**
 * A "bean" implementation of the BeanComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1BeanComponentImplementationModel extends V1ComponentImplementationModel implements BeanComponentImplementationModel {

    /**
     * Default constructor for application use.
     */
    public V1BeanComponentImplementationModel() {
        super(BEAN, DEFAULT_NAMESPACE);
    }

    /**
     * Constructor for Marshaller use (ie: V1BeanMarshaller).
     *
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1BeanComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getClazz() {
        return getModelAttribute(CLASS);
    }

    @Override
    public BeanComponentImplementationModel setClazz(String clazz) {
        setModelAttribute(CLASS, clazz);
        return this;
    }

}
