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
package org.switchyard.config.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

import org.switchyard.config.Configuration;
import org.switchyard.config.OutputKey;

/**
 * The central and most important interface of the Model API.  A Model "wraps" a {@link org.switchyard.config.Configuration Configuration}.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface Model {

    /**
     * Gets the wrapped config for this model.
     * @return the underlying config
     */
    public Configuration getModelConfiguration();

    /**
     * Gets the Descriptor used to create this model.
     * @return the descriptor
     */
    public Descriptor getModelDescriptor();

    /**
     * Gets the parent model of this model, if there is one.
     * @return this model's parent model, or null if there isn't one
     */
    public Model getModelParent();

    /**
     * Gets all the child models of this model.
     * @return a list of this model's children
     */
    public List<Model> getModelChildren();

    /**
     * Orders the children based on what was set via {@link #setModelChildrenOrder(String...)}.
     * @return this model (useful for chaining)
     */
    public Model orderModelChildren();

    /**
     * Validates this model against schema(s) defined in this model's descriptor.
     * @return the result of the validation
     */
    public Validation validateModel();

    /**
     * Whether or not this model is valid, according to the schema(s) defined in this model's descriptor.
     * @return true if the model is valid
     */
    public boolean isModelValid();

    /**
     * Writes this model out in it's native form (which most likely means writing out the wrapped Configuration).
     * @param out the OutputStream to write to
     * @param keys the OutputKeys to respect
     * @throws IOException if a problem occurs
     */
    public void write(OutputStream out, OutputKey... keys) throws IOException;

    /**
     * Writes this model out in it's native form (which most likely means writing out the wrapped Configuration).
     * @param writer the Writer to write to
     * @param keys the OutputKeys to respect
     * @throws IOException if a problem occurs
     */
    public void write(Writer writer, OutputKey... keys) throws IOException;

}
