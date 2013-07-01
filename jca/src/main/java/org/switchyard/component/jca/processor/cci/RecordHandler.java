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
package org.switchyard.component.jca.processor.cci;

import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.RecordFactory;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.composer.JCAComposition;
import org.switchyard.component.jca.composer.RecordBindingData;
import org.switchyard.component.jca.config.model.JCABindingModel;

/**
 * RecordHandler.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 * @param <D> record binding data type
 */
public abstract class RecordHandler<D extends RecordBindingData<?>> {

    private MessageComposer<D> _composer;
    private JCABindingModel _jcaBindingModel;
    private RecordFactory _recordFactory;
    private InteractionSpec _interactionSpec;
    
    /**
     * handle interaction.
     * 
     * @param exchange SwitchYard Exchange instance
     * @param conn Connection instance
     * @param interaction Interaction instance
     * @return reply message
     * @throws Exception when interaction failed
     */
    public abstract Message handle(Exchange exchange, Connection conn, Interaction interaction) throws Exception;

    /**
     * set JCA binding model.
     * @param model JCA binding model
     * @return this instance for method chaining
     */
    public RecordHandler<D> setJCABindingModel(JCABindingModel model) {
        _jcaBindingModel = model;
        return this;
    }
    
    /**
     * get JCA binding model.
     * @return JCA binding model
     */
    public JCABindingModel getJCABindingModel() {
        return _jcaBindingModel;
    }
    
    /**
     * set RecordFactory.
     * @param factory RecordFactory
     * @return this instance for method chaining
     */
    public RecordHandler<D> setRecordFactory(RecordFactory factory) {
        _recordFactory = factory;
        return this;
    }
    
    /**
     * get RecordFactory.
     * @return RecordFactory
     */
    public RecordFactory getRecordFactory() {
        return _recordFactory;
    }
    
    /**
     * set InteractionSpec.
     * @param interactionSpec InteractionSpec
     * @return this instance for method chaining
     */
    public RecordHandler<D> setInteractionSpec(InteractionSpec interactionSpec) {
        _interactionSpec = interactionSpec;
        return this;
    }
    
    /**
     * get InteractionSpec.
     * @return InteractionSpec
     */
    public InteractionSpec getInteractionSpec() {
        return _interactionSpec;
    }

    protected MessageComposer<D> getMessageComposer(Class<D> clazz) {
        if (_composer == null) {
            _composer = JCAComposition.getMessageComposer(_jcaBindingModel, clazz);
        }
        return _composer;
    }
}
