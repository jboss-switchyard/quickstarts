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
import org.switchyard.component.jca.config.model.InboundConnectionModel;
import org.switchyard.component.jca.config.model.InboundInteractionModel;
import org.switchyard.component.jca.config.model.JCABindingModel;
import org.switchyard.component.jca.config.model.OutboundConnectionModel;
import org.switchyard.component.jca.config.model.OutboundInteractionModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1BindingModel;

/**
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1JCABindingModel extends V1BindingModel implements JCABindingModel {

    /**
     * Constructor.
     * @param namespace namespace
     */
    public V1JCABindingModel(String namespace) {
        super(JCAConstants.JCA, namespace);
        setModelChildrenOrder(JCAConstants.OUTBOUND_CONNECTION,
                                JCAConstants.INBOUND_CONNECTION,
                                JCAConstants.OUTBOUND_INTERACTION,
                                JCAConstants.INBOUND_INTERACTION,
                                JCAConstants.WIRE_FORMAT);
    }

    /**
     * Constractor.
     * 
     * @param config configuration
     * @param desc description
     */
    public V1JCABindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(JCAConstants.OUTBOUND_CONNECTION,
                JCAConstants.INBOUND_CONNECTION,
                JCAConstants.OUTBOUND_INTERACTION,
                JCAConstants.INBOUND_INTERACTION,
                JCAConstants.WIRE_FORMAT);
    }

    @Override
    public OutboundConnectionModel getOutboundConnection() {
        return (OutboundConnectionModel)getFirstChildModel(JCAConstants.OUTBOUND_CONNECTION);
    }

    @Override
    public JCABindingModel setOutboundConnection(final OutboundConnectionModel config) {
        setChildModel(config);
        return this;
    }

    @Override
    public InboundConnectionModel getInboundConnection() {
        return (InboundConnectionModel)getFirstChildModel(JCAConstants.INBOUND_CONNECTION);
    }

    @Override
    public JCABindingModel setInboundConnection(InboundConnectionModel config) {
        setChildModel(config);
        return this;
    }

    @Override
    public OutboundInteractionModel getOutboundInteraction() {
        return (OutboundInteractionModel)getFirstChildModel(JCAConstants.OUTBOUND_INTERACTION);
    }

    @Override
    public JCABindingModel setOutboundInteraction(OutboundInteractionModel config) {
        setChildModel(config);
        return this;
    }

    @Override
    public InboundInteractionModel getInboundInteraction() {
        return (InboundInteractionModel)getFirstChildModel(JCAConstants.INBOUND_INTERACTION);
    }

    @Override
    public JCABindingModel setInboundInteraction(InboundInteractionModel config) {
        setChildModel(config);
        return this;
    }


}
