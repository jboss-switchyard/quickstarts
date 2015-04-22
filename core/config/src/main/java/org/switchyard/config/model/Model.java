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
     * Gets the root model of this model, or this model if this is the root.
     * @return this model's root, or this model if this is the root
     */
    public Model getModelRoot();

    /**
     * Gets the model's root namespace.
     * @return this model's root namespace
     */
    public String getModelRootNamespace();

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
     * Asserts that this model is valid, according to the schema(s) defined in this model's descriptor.
     * @return this model (useful for chaining)
     */
    public Model assertModelValid();

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
