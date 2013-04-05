/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
