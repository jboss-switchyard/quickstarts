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

import javax.xml.namespace.QName;

import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.jca.config.model.ActivationSpecModel;
import org.switchyard.component.jca.config.model.InboundConnectionModel;
import org.switchyard.component.jca.config.model.ResourceAdapterModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * V1 InboundConnection model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1InboundConnectionModel extends BaseModel implements InboundConnectionModel {

    /**
     * Constructor.
     */
    public V1InboundConnectionModel() {
        super(new QName(JCAConstants.DEFAULT_NAMESPACE, JCAConstants.INBOUND_CONNECTION));
        setModelChildrenOrder(JCAConstants.RESOURCE_ADAPTER, JCAConstants.ACTIVATION_SPEC);
    }
    
    /**
     * Constructor.
     * 
     * @param config configuration
     * @param desc descriptor
     */
    public V1InboundConnectionModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public ResourceAdapterModel getResourceAdapter() {
        return (ResourceAdapterModel) getFirstChildModel(JCAConstants.RESOURCE_ADAPTER);
    }

    @Override
    public InboundConnectionModel setResourceAdapter(ResourceAdapterModel config) {
        setChildModel(config);
        return this;
    }

    @Override
    public ActivationSpecModel getActivationSpec() {
        return (ActivationSpecModel) getFirstChildModel(JCAConstants.ACTIVATION_SPEC);
    }

    @Override
    public InboundConnectionModel setActivationSpec(ActivationSpecModel config) {
        setChildModel(config);
        return this;
    }

}
