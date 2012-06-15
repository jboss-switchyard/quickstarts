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
import org.switchyard.component.jca.config.model.EndpointModel;
import org.switchyard.component.jca.config.model.InboundInteractionModel;
import org.switchyard.component.jca.config.model.InboundOperationModel;
import org.switchyard.component.jca.config.model.ListenerModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * V1 InboundInteraction model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1InboundInteractionModel extends BaseModel implements InboundInteractionModel {

    /**
     * Constructor.
     */
    public V1InboundInteractionModel() {
        super(new QName(JCAConstants.DEFAULT_NAMESPACE, JCAConstants.INBOUND_INTERACTION));
        setModelChildrenOrder(JCAConstants.LISTENER, JCAConstants.INBOUND_OPERATION);
    }
    
    /**
     * Constructor.
     * 
     * @param config configuration
     * @param desc description
     */
    public V1InboundInteractionModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public ListenerModel getListener() {
        return (ListenerModel) getFirstChildModel(JCAConstants.LISTENER);
    }

    @Override
    public InboundInteractionModel setListener(ListenerModel listener) {
        setChildModel(listener);
        return this;
    }

    @Override
    public InboundOperationModel getInboundOperation() {
        return (InboundOperationModel) getFirstChildModel(JCAConstants.INBOUND_OPERATION);
    }

    @Override
    public InboundInteractionModel setInboundOperation(InboundOperationModel operation) {
        setChildModel(operation);
        return this;
    }

    @Override
    public EndpointModel getEndpoint() {
        return (EndpointModel) getFirstChildModel(JCAConstants.ENDPOINT);
    }

    @Override
    public InboundInteractionModel setEndpoint(EndpointModel endpoint) {
        setChildModel(endpoint);
        return this;
    }

    @Override
    public boolean isTransacted() {
        Configuration config = getModelConfiguration().getFirstChild(JCAConstants.TRANSACTED);
        return config != null ? Boolean.parseBoolean(config.getValue()) : false;
    }

    @Override
    public InboundInteractionModel setTransacted(boolean transacted) {
        Configuration config = getModelConfiguration().getFirstChild(JCAConstants.TRANSACTED);
        if (config != null) {
            config.setValue(Boolean.toString(transacted));
        } else {
            V1NameValueModel model = new V1NameValueModel(JCAConstants.TRANSACTED);
            model.setValue(Boolean.toString(transacted));
            setChildModel(model);
        }
        return this;
    }

}
