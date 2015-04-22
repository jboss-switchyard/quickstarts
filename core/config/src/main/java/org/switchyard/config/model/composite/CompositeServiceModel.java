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

import org.switchyard.config.model.NamedModel;

/**
 * The "composite/service" model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface CompositeServiceModel extends NamedModel {

    /** The "service" name. */
    public static final String SERVICE = "service";

    /** The "promote" name. */
    public static final String PROMOTE = "promote";

    /**
     * Gets the parent composite model.
     * @return the parent composite model
     */
    public CompositeModel getComposite();

    /**
     * Gets the child component service model.
     * @return the child component service model
     */
    public ComponentServiceModel getComponentService();

    /**
     * Gets the promote attribute.
     * @return the promote attribute
     */
    public String getPromote();

    /**
     * Sets the promote attribute.
     * @param promote the promote attribute
     * @return this CompositeServiceModel (useful for chaining)
     */
    public CompositeServiceModel setPromote(String promote);

    /**
     * Gets the child binding models.
     * @return the child binding models
     */
    public List<BindingModel> getBindings();

    /**
     * Adds a child binding model.
     * @param binding the child binding model
     * @return this CompositeServiceModel (useful for chaining)
     */
    public CompositeServiceModel addBinding(BindingModel binding);
    
    /**
     * Gets the child service interface model.
     * @return the child service interface model
     */
    public InterfaceModel getInterface();

    /**
     * Sets the child service interface model.
     * @param interfaze child service interface model
     * @return this CompositeServiceModel (useful for chaining)
     */
    public CompositeServiceModel setInterface(InterfaceModel interfaze);

    /**
     * Gets the child extensions model.
     * @return the child extensions model
     */
    public ExtensionsModel getExtensions();
    
    /**
     * Sets the child extensions model.
     * @param extensions child extensions model
     * @return this CompositeServiceModel (useful for chaining)
     */
    public CompositeServiceModel setExtensions(ExtensionsModel extensions);
}
