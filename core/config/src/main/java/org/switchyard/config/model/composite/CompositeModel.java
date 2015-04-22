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
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * The "composite" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface CompositeModel extends NamedModel, PropertyResolver {

    /** The "composite" name. */
    public static final String COMPOSITE = "composite";

    /**
     * Gets the parent switchyard model.
     * @return the parent switchyard model
     */
    public SwitchYardModel getSwitchYard();

    /**
     * Gets the child composite service models.
     * @return the child composite service models
     */
    public List<CompositeServiceModel> getServices();

    /**
     * Adds a child composite service model.
     * @param service the child composite service model
     * @return this CompositeModel (useful for chaining)
     */
    public CompositeModel addService(CompositeServiceModel service);

    /**
     * Gets the child composite reference models.
     * @return the child composite reference models
     */
    public List<CompositeReferenceModel> getReferences();

    /**
     * Adds a composite reference model.
     * @param reference the composite reference model
     * @return this CompositeModel (useful for chaining)
     */
    public CompositeModel addReference(CompositeReferenceModel reference);

    /**
     * Gets the child component models.
     * @return the child component models
     */
    public List<ComponentModel> getComponents();

    /**
     * Adds a child component model.
     * @param component the child component model
     * @return this CompositeModel (useful for chaining)
     */
    public CompositeModel addComponent(ComponentModel component);
    
    /**
     * Sets the target namespace for the composite.
     * @param namespaceUri namespace URI
     */
    public void setTargetNamespace(String namespaceUri);

    /**
     * Gets a child property model.
     * @param name property name to get
     * @return the child property model
     */
    public PropertyModel getProperty(String name);

    /**
     * Gets the map of child property model.
     * @return map of child property model
     */
    public Map<String, PropertyModel> getProperties();
    
    /**
     * Sets the child property model.
     * @param property the child property model
     * @return this CompositeModel (useful for chaining)
     */
    public CompositeModel addProperty(PropertyModel property);

    /**
     * Sets the composite property resolver based on the current state of the model.
     */
    public void setCompositePropertyResolver();
    
}
