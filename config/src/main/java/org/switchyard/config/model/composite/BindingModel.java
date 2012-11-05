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
