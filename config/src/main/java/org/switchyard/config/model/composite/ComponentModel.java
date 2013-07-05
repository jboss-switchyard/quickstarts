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
