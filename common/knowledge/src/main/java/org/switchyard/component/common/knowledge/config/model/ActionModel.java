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

import org.switchyard.component.common.knowledge.ActionType;
import org.switchyard.config.model.Model;

/**
 * An Action Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface ActionModel extends Model {

    /** The "action" name. */
    public static final String ACTION = "action";

    /**
     * Gets the eventId attribute.
     * @return the eventId attribute
     */
    public String getEventId();

    /**
     * Sets the eventId attribute.
     * @param eventId the eventId attribute
     * @return this ActionModel (useful for chaining)
     */
    public ActionModel setEventId(String eventId);

    /**
     * Gets the operation attribute.
     * @return the operation attribute
     */
    public String getOperation();

    /**
     * Sets the operation attribute.
     * @param operation the operation attribute
     * @return this ActionModel (useful for chaining)
     */
    public ActionModel setOperation(String operation);

    /**
     * Gets the type attribute.
     * @return the type attribute
     */
    public ActionType getType();

    /**
     * Sets the type attribute.
     * @param type the type attribute
     * @return this ActionModel (useful for chaining)
     */
    public ActionModel setType(ActionType type);

    /**
     * Gets the child globals mappings model.
     * @return the child globals mappings model
     */
    public GlobalsModel getGlobals();

    /**
     * Sets the child globals mappings model.
     * @param globals the child globals mappings model
     * @return this ActionModel (useful for chaining)
     */
    public ActionModel setGlobals(GlobalsModel globals);

    /**
     * Gets the child inputs mappings model.
     * @return the child inputs mappings model
     */
    public InputsModel getInputs();

    /**
     * Sets the child inputs mappings model.
     * @param inputs the child inputs mappings model
     * @return this ActionModel (useful for chaining)
     */
    public ActionModel setInputs(InputsModel inputs);

    /**
     * Gets the child outputs mappings model.
     * @return the child outputs mappings model
     */
    public OutputsModel getOutputs();

    /**
     * Sets the child outputs mappings model.
     * @param outputs the child outputs mappings model
     * @return this ActionModel (useful for chaining)
     */
    public ActionModel setOutputs(OutputsModel outputs);

}
