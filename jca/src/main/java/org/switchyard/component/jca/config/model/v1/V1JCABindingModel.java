/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
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
     */
    public V1JCABindingModel() {
        super(JCAConstants.JCA, JCAConstants.DEFAULT_NAMESPACE);
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
