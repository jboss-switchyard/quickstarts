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
import org.switchyard.component.jca.config.model.ConnectionModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * V1 Connection model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1ConnectionModel extends V1BasePropertyContainerModel implements ConnectionModel {

    /**
     * Constructor.
     * @param namespace namespace
     */
    public V1ConnectionModel(String namespace) {
        super(namespace, JCAConstants.CONNECTION);
        setModelChildrenOrder(JCAConstants.PROPERTY);
    }
    
    /**
     * Constructor.
     * 
     * @param config configuration
     * @param desc descriptor
     */
    public V1ConnectionModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getConnectionFactoryJNDIName() {
        return getModelAttribute(JCAConstants.JNDI_NAME);
    }

    @Override
    public ConnectionModel setConnectionFactoryJNDIName(String name) {
        setModelAttribute(JCAConstants.JNDI_NAME, name);
        return this;
    }

}
