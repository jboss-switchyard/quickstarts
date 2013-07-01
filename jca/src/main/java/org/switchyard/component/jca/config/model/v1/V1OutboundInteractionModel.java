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
import org.switchyard.component.jca.config.model.ConnectionSpecModel;
import org.switchyard.component.jca.config.model.InteractionSpecModel;
import org.switchyard.component.jca.config.model.OperationModel;
import org.switchyard.component.jca.config.model.OutboundInteractionModel;
import org.switchyard.component.jca.config.model.ProcessorModel;
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
        setModelChildrenOrder(JCAConstants.CONNECTION_SPEC,
                                JCAConstants.INTERACTION_SPEC,
                                JCAConstants.OPERATION,
                                JCAConstants.PROCESSOR);
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

    @Override
    public ProcessorModel getProcessor() {
        return (ProcessorModel) getFirstChildModel(JCAConstants.PROCESSOR);
    }

    @Override
    public OutboundInteractionModel setProcessor(ProcessorModel model) {
        setChildModel(model);
        return this;
    }

}
