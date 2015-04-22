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
package org.switchyard.component.jca.config.model.v1;

import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.jca.config.model.InteractionSpecModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * V1 InteractionSpec model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1InteractionSpecModel extends V1BasePropertyContainerModel implements InteractionSpecModel {

    /**
     * Constructor.
     * @param namespace namespace
     */
    public V1InteractionSpecModel(String namespace) {
        super(namespace, JCAConstants.INTERACTION_SPEC);
        setModelChildrenOrder(JCAConstants.PROPERTY);
    }
    
    /**
     * Constructor.
     * 
     * @param config configuration
     * @param desc descriptor
     */
    public V1InteractionSpecModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getInteractionSpecClassName() {
        return getModelAttribute(JCAConstants.TYPE);
    }

    @Override
    public InteractionSpecModel setInteractionSpecClassName(String name) {
        setModelAttribute(JCAConstants.TYPE, name);
        return this;
    }

}
