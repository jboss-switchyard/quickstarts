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

import org.switchyard.config.Configuration;

/**
 * Used to read or write Models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface Marshaller {

    /**
     * Gets the Descriptor used by this Marshaller.
     * @return the Descriptor
     */
    public Descriptor getDescriptor();

    /**
     * Reads in (constructs) a Model based on the specified Configuration.
     * @param config the config
     * @return the new Model
     */
    public Model read(Configuration config);

    /**
     * Writes the specified Model to the specified OutputStream.
     * @param model the model
     * @param out the OutputStream
     * @throws IOException if a problem occurred
     */
    public void write(Model model, OutputStream out) throws IOException;

    /**
     * Writes the specified Model to the specified Writer.
     * @param model the model
     * @param writer the Writer
     * @throws IOException if a problem occurred
     */
    public void write(Model model, Writer writer) throws IOException;

}
