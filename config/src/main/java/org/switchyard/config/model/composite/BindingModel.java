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

import org.switchyard.config.model.TypedModel;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.selector.OperationSelectorModel;

/**
 * The "binding" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface BindingModel extends TypedModel {

    /** The "binding" name. */
    public static final String BINDING = "binding";

    /**
     * Gets the name <b>attribute</b> of this Model (<i>not</i> the name of the wrapped Configuration).
     * @return the name
     */
    public String getName();

    /**
     * Sets the name <b>attribute</b> of this Model (<i>not</i> the name of the wrapped Configuration).
     * @param name the name
     * @return this BindingModel (useful for chaining)
     */
    public BindingModel setName(String name);

    /**
     * Gets the parent service model.  This method will return null if 
     * the binding model is attached to a reference instead of a service.
     * @return the parent composite model
     */
    public CompositeServiceModel getService();
    
    /**
     * Gets the parent reference model.  This method will return null if 
     * the binding model is attached to a service instead of a reference.
     * @return the parent composite model
     */
    public CompositeReferenceModel getReference();
    
    /**
     * Is this binding attached to a service?
     * @return true if this is a service binding, false if it's a reference binding.
     */
    public boolean isServiceBinding();
    
    /**
     * Is this binding attached to a reference?
     * @return true if this is a reference binding, false if it's a service binding.
     */
    public boolean isReferenceBinding();

    /**
     * Gets the child operation selector model.
     * @return the child operation selector model
     */
    public OperationSelectorModel getOperationSelector();
    
    /**
     * Sets the child operation selector model.
     * @param model the child operation selector model
     * @return this model(useful for chaining) 
     */
    public BindingModel setOperationSelector(OperationSelectorModel model);
    
    /**
     * Gets the child context mapper model.
     * @return the child context mapper model
     */
    public ContextMapperModel getContextMapper();

    /**
     * Gets the child message composer model.
     * @return the child message composer model
     */
    public MessageComposerModel getMessageComposer();

    /**
     * Sets the child context mapper model.
     * @param model the context mapper model to set
     * @return this BindingModel (useful for chaining)
     */
    public BindingModel setContextMapper(ContextMapperModel model);
    
    /**
     * Sets the child message composer model.
     * @param model the message composer model to set
     * @return this BindingModel (useful for chaining)
     */
    public BindingModel setMessageComposer(MessageComposerModel model);
}
