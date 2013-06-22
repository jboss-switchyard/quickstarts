/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
