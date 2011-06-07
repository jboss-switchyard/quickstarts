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

import javax.xml.namespace.QName;

import org.switchyard.config.model.NamedModel;

/**
 * The "composite/reference" model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface CompositeReferenceModel extends NamedModel {

    /** The "reference" name. */
    public static final String REFERENCE = "reference";

    /** The "promote" name. */
    public static final String PROMOTE = "promote";

    /**
     * Gets the parent composite model.
     * @return the parent composite model
     */
    public CompositeModel getComposite();

    /**
     * Gets the child component reference model.
     * @return the child component reference model
     */
    public ComponentReferenceModel getComponentReference();

    /**
     * Gets the promote attribute.
     * @return the promote attribute
     */
    public QName getPromote();

    /**
     * Sets the promote attribute.
     * @param promote the promote attribute
     * @return this CompositeReferenceModel (useful for chaining)
     */
    public CompositeReferenceModel setPromote(QName promote);

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
