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
 * The "composite/reference" model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface CompositeReferenceModel extends NamedModel {

    /** The "multiplicity" name. */
    public static final String MULTIPLICITY = "multiplicity";
    /** The default multiplicity (1..1). */
    public static final String DEFAULT_MULTIPLICITY = "1..1";

    /** The "promote" name. */
    public static final String PROMOTE = "promote";

    /** The "reference" name. */
    public static final String REFERENCE = "reference";

    /**
     * Gets the parent composite model.
     * @return the parent composite model
     */
    public CompositeModel getComposite();

    /**
     * Gets the child component reference models.
     * @return the child component reference models
     */
    public List<ComponentReferenceModel> getComponentReferences();

    /**
     * Gets the promote attribute.
     * @return the promote attribute
     */
    public String getPromote();

    /**
     * Sets the promote attribute.
     * @param promote the promote attribute
     * @return this CompositeReferenceModel (useful for chaining)
     */
    public CompositeReferenceModel setPromote(String promote);

    /**
     * Gets the multiplicity attribute.
     * @return the multiplicity attribute
     */
    public String getMultiplicity();

    /**
     * Sets the multiplicity attribute.
     * @param multiplicity the multiplicity attribute
     * @return this CompositeReferenceModel (useful for chaining)
     */
    public CompositeReferenceModel setMultiplicity(String multiplicity);

    /**
     * Gets the child binding models.
     * @return the child binding models
     */
    public List<BindingModel> getBindings();

    /**
     * Adds a child binding model.
     * @param binding the child binding model
     * @return this CompositeReferenceModel (useful for chaining)
     */
    public CompositeReferenceModel addBinding(BindingModel binding);

    /**
     * Gets the child reference interface model.
     * @return the child reference interface model
     */
    public InterfaceModel getInterface();

    /**
     * Sets the child reference interface model.
     * @param interfaze the child reference interface model
     * @return this CompositeReferenceModel (useful for chaining)
     */
    public CompositeReferenceModel setInterface(InterfaceModel interfaze);

}

