/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.model.v2;

import org.switchyard.common.type.Classes;
import org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 2nd version ExtraJaxbClassModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class V2ExtraJaxbClassModel extends BaseModel implements ExtraJaxbClassModel {

    /**
     * Creates a new ExtraJaxbClassModel in the specified namespace.
     * @param namespace the namespace
     */
    public V2ExtraJaxbClassModel(String namespace) {
        super(namespace, EXTRA_JAXB_CLASS);
    }

    /**
     * Creates a new ExtraJaxbClassModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V2ExtraJaxbClassModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getClazz(ClassLoader loader) {
        String c = getModelValue();
        return c != null ? Classes.forName(c, loader) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtraJaxbClassModel setClazz(Class<?> clazz) {
        String c = clazz != null ? clazz.getName() : null;
        setModelValue(c);
        return this;
    }

}
