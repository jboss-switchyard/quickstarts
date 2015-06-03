/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel.common.model;

import org.switchyard.config.model.Model;

/**
 * "parameter" configuration model.
 */
public interface ParameterModel extends Model {

    /** The "parameter" name. */
    public static final String PARAMETER = "parameter";

    /**
     * Gets the parent parameters model.
     * @return the parent parameters model.
     */
    public AdditionalUriParametersModel getAdditionalUriParameters();

    /**
     * Gets the name attribute.
     * @return the name attribute
     */
    public String getName();

    /**
     * Sets the name attribute.
     * @param name the name attribute
     * @return this ParameterModel (useful for chaining)
     */
    public ParameterModel setName(String name);

    /**
     * Gets the value attribute.
     * @return the value attribute
     */
    public String getValue();

    /**
     * Sets the value attribute.
     * @param value the value attribute
     * @return this ParameterModel (useful for chaining)
     */
    public ParameterModel setValue(String value);

}
