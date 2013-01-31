/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

    /** The default SCA namespace. */
    public static final String DEFAULT_NAMESPACE = "http://docs.oasis-open.org/ns/opencsa/sca/200912";

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
