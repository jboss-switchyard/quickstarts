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

package org.switchyard.config.model.composite.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseTypedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.composite.SCANamespace;

/**
 * A representation of an InterfaceModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1InterfaceModel extends BaseTypedModel implements InterfaceModel {
    
    /**
     * Creates a new, empty interface model of the specified type.
     * @param type interface type
     */
    public V1InterfaceModel(String type) {
        this(type, SCANamespace.DEFAULT.uri());
    }

    /**
     * Creates a new, empty interface model with the specified type and namespace.
     * @param type interface type
     * @param namespace namespace
     */
    public V1InterfaceModel(String type, String namespace) {
        super(new QName(namespace, InterfaceModel.INTERFACE + '.' + type));
    }

    /**
     * Creates a new InterfaceModel from the specified configuration.
     * @param config configuration
     * @param desc descriptor
     */
    public V1InterfaceModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInterface() {
        return getModelAttribute(InterfaceModel.INTERFACE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InterfaceModel setInterface(String interfaze) {
        setModelAttribute(InterfaceModel.INTERFACE, interfaze);
        return this;
    }

}
