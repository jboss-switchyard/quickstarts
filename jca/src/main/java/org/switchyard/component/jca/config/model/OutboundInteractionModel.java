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
package org.switchyard.component.jca.config.model;

import org.switchyard.config.model.Model;

/**
 * OutboundInteraction model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public interface OutboundInteractionModel extends Model {

    /**
     * get ConnectionSpec model.
     * 
     * @return {@link ConnectionSpecModel}
     */
    ConnectionSpecModel getConnectionSpec();
    
    /**
     * set ConnectionSpec model.
     * 
     * @param model {@link ConnectionSpecModel} to set
     * @return {@link OutboundInteractionModel} to support method chaining
     */
    OutboundInteractionModel setConnectionSpec(final ConnectionSpecModel model);
    
    /**
     * get InteractionSpec model.
     * 
     * @return {@link InteractionSpecModel}
     */
    InteractionSpecModel getInteractionSpec();
    
    /**
     * set InteractionSpec model.
     * 
     * @param model {@link InteractionSpecModel} to set
     * @return {@link OutboundInteractionModel} to support method chaining
     */
    OutboundInteractionModel setInteractionSpec(final InteractionSpecModel model);
    
    /**
     * get Operation model.
     * 
     * @return {@link OperationModel}
     */
    OperationModel getOperation();
    
    /**
     * set Operation model.
     * 
     * @param model {@link OperationModel} to set
     * @return {@link OutboundInteractionModel} to support method chaining
     */
    OutboundInteractionModel setOperation(final OperationModel model);

    /**
     * get Processor model.
     * 
     * @return {@link ProcessorModel}
     */
    ProcessorModel getProcessor();
    
    /**
     * set Processor model.
     * 
     * @param model {@link ProcessorModel} to set
     * @return {@link OutboundInteractionModel} to support method chaining
     */
    OutboundInteractionModel setProcessor(ProcessorModel model);
}
