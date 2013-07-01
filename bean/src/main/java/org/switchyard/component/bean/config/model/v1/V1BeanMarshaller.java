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
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.v1.V1CompositeMarshaller;

/**
 * A CompositeMarshaller which can also create BeanComponentImplementationModels.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1BeanMarshaller extends V1CompositeMarshaller {

    /**
     * The complete local name ("implementation.bean").
     */
    private static final String IMPLEMENTATION_BEAN = ComponentImplementationModel.IMPLEMENTATION + "." + BeanComponentImplementationModel.BEAN;

    /**
     * Required constructor called via reflection.
     *
     * @param desc the Descriptor
     */
    public V1BeanMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads in the Configuration, looking for "implementation.bean".
     * If not found, it falls back to the super class (V1CompositeMarshaller).
     *
     * @param config the Configuration
     * @return the Model
     */
    @Override
    public Model read(Configuration config) {
        if (config.getName().equals(IMPLEMENTATION_BEAN)) {
            return new V1BeanComponentImplementationModel(config, getDescriptor());
        }
        return super.read(config);
    }

}
