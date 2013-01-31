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

package org.switchyard.config.model.composite;

import java.util.List;
import java.util.Map;

import org.switchyard.common.property.PropertyResolver;
import org.switchyard.config.model.NamedModel;
import org.switchyard.config.model.property.PropertyModel;

/**
 * The "component" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ComponentModel extends NamedModel, PropertyResolver {

    /** The "component" name. */
    public static final String COMPONENT = "component";

    /**
     * Gets the parent composite model.
     * @return the parent composite model
     */
    public CompositeModel getComposite();

    /**
     * Gets the child component implementation model.
     * @return the child component implementation model
     */
    public ComponentImplementationModel getImplementation();

    /**
     * Sets the child copmonent implementation model.
     * @param implementation the child component implementation model
     * @return this ComponentModel (useful for chaining)
     */
    public ComponentModel setImplementation(ComponentImplementationModel implementation);

    /**
     * Gets the child component service models.
     * @return the child component service models
     */
    public List<ComponentServiceModel> getServices();

    /**
     * Adds a child component service model.
     * @param service the child component service model
     * @return this ComponentModel (useful for chaining)
     */
    public ComponentModel addService(ComponentServiceModel service);

    /**
     * Gets the child component reference models.
     * @return the child component reference models
     */
    public List<ComponentReferenceModel> getReferences();

    /**
     * Adds a child component reference model.
     * @param reference the child component reference model
     * @return this ComponentModel (useful for chaining)
     */
    public ComponentModel addReference(ComponentReferenceModel reference);

    /**
     * Gets a child property model.
     * @param name property name to get
     * @return the child property model
     */
    public PropertyModel getProperty(String name);

    /**
     * Gets the map of child property model.
     * @return map of the child property model
     */
    public Map<String, PropertyModel> getProperties();
    
    /**
     * Sets the child properties model.
     * @param property the child property model
     * @return this ComponentModel (useful for chaining)
     */
    public ComponentModel addProperty(PropertyModel property);

    /**
     * Sets the component property resolver based on the current state of the model.
     */
    public void setComponentPropertyResolver();
}
