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
package org.switchyard.component.common.knowledge.config.model;

import org.switchyard.component.common.knowledge.OperationType;
import org.switchyard.config.model.NamedModel;

/**
 * An Operation Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface OperationModel extends NamedModel {

    /** The "operation" name. */
    public static final String OPERATION = "operation";

    /**
     * Gets the eventId attribute.
     * @return the eventId attribute
     */
    public String getEventId();

    /**
     * Sets the eventId attribute.
     * @param eventId the eventId attribute
     * @return this OperationModel (useful for chaining)
     */
    public OperationModel setEventId(String eventId);

    /**
     * Gets the type attribute.
     * @return the type attribute
     */
    public OperationType getType();

    /**
     * Sets the type attribute.
     * @param type the type attribute
     * @return this OperationModel (useful for chaining)
     */
    public OperationModel setType(OperationType type);

    /**
     * Gets the child globals mappings model.
     * @return the child globals mappings model
     */
    public GlobalsModel getGlobals();

    /**
     * Sets the child globals mappings model.
     * @param globals the child globals mappings model
     * @return this OperationModel (useful for chaining)
     */
    public OperationModel setGlobals(GlobalsModel globals);

    /**
     * Gets the child inputs mappings model.
     * @return the child inputs mappings model
     */
    public InputsModel getInputs();

    /**
     * Sets the child inputs mappings model.
     * @param inputs the child inputs mappings model
     * @return this OperationModel (useful for chaining)
     */
    public OperationModel setInputs(InputsModel inputs);

    /**
     * Gets the child outputs mappings model.
     * @return the child outputs mappings model
     */
    public OutputsModel getOutputs();

    /**
     * Sets the child outputs mappings model.
     * @param outputs the child outputs mappings model
     * @return this OperationModel (useful for chaining)
     */
    public OperationModel setOutputs(OutputsModel outputs);

    /**
     * Gets the child faults mappings model.
     * @return the child faults mappings model
     */
    public FaultsModel getFaults();

    /**
     * Sets the child faults mappings model.
     * @param faults the child faults mappings model
     * @return this OperationModel (useful for chaining)
     */
    public OperationModel setFaults(FaultsModel faults);

}
