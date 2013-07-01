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
import org.switchyard.component.jca.config.model.ConnectionModel;
import org.switchyard.component.jca.config.model.OutboundConnectionModel;
import org.switchyard.component.jca.config.model.ResourceAdapterModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * V1 OutboundConnection Model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 * 
 */
public class V1OutboundConnectionModel extends BaseModel implements OutboundConnectionModel {

    /**
     * Constructor.
     */
    public V1OutboundConnectionModel() {
        super(new QName(JCAConstants.DEFAULT_NAMESPACE, JCAConstants.OUTBOUND_CONNECTION));
        setModelChildrenOrder(JCAConstants.RESOURCE_ADAPTER, JCAConstants.CONNECTION);
    }
    
    /**
     * Constructor.
     * 
     * @param config configuration
     * @param desc descriptor
     */
    public V1OutboundConnectionModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public ResourceAdapterModel getResourceAdapter() {
        return (ResourceAdapterModel) getFirstChildModel(JCAConstants.RESOURCE_ADAPTER);
    }

    @Override
    public OutboundConnectionModel setResourceAdapter(ResourceAdapterModel model) {
        setChildModel(model);
        return this;
    }

    @Override
    public ConnectionModel getConnection() {
        return (ConnectionModel) getFirstChildModel(JCAConstants.CONNECTION);
    }

    @Override
    public OutboundConnectionModel setConnection(ConnectionModel model) {
        setChildModel(model);
        return this;
    }

    @Override
    public boolean isManaged() {
        String managed = getModelAttribute(JCAConstants.MANAGED);
        if (managed == null) {
            setManaged(true);
            managed = "true";
        }
        
        return Boolean.parseBoolean(managed);
    }
    
    @Override
    public OutboundConnectionModel setManaged(boolean managed) {
        setModelAttribute(JCAConstants.MANAGED, Boolean.toString(managed));
        return this;
    }
}
