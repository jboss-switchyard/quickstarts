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

import javax.xml.namespace.QName;

import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.jca.config.model.ConnectionSpecModel;
import org.switchyard.component.jca.config.model.InteractionSpecModel;
import org.switchyard.component.jca.config.model.OperationModel;
import org.switchyard.component.jca.config.model.OutboundInteractionModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * V1 OutboundInteraction model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1OutboundInteractionModel extends BaseModel implements OutboundInteractionModel {

    /**
     * Constructor.
     */
    public V1OutboundInteractionModel() {
        super(new QName(JCAConstants.DEFAULT_NAMESPACE, JCAConstants.OUTBOUND_INTERACTION));
        setModelChildrenOrder(JCAConstants.CONNECTION_SPEC, JCAConstants.INTERACTION_SPEC, JCAConstants.OPERATION);
    }
    
    /**
     * Constructor.
     * 
     * @param config configuration 
     * @param desc descriptor
     */
    public V1OutboundInteractionModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public ConnectionSpecModel getConnectionSpec() {
        return (ConnectionSpecModel) getFirstChildModel(JCAConstants.CONNECTION_SPEC);
    }

    @Override
    public OutboundInteractionModel setConnectionSpec(ConnectionSpecModel model) {
        setChildModel(model);
        return this;
    }

    @Override
    public InteractionSpecModel getInteractionSpec() {
        return (InteractionSpecModel) getFirstChildModel(JCAConstants.INTERACTION_SPEC);
    }

    @Override
    public OutboundInteractionModel setInteractionSpec(InteractionSpecModel model) {
        setChildModel(model);
        return this;
    }

    @Override
    public OperationModel getOperation() {
        return (OperationModel) getFirstChildModel(JCAConstants.OPERATION);
    }

    @Override
    public OutboundInteractionModel setOperation(OperationModel model) {
        setChildModel(model);
        return this;
    }

}
