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
 * Abstract representation of a Marshaller, implementing all the methods with default implementations.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseMarshaller implements Marshaller {

    private Descriptor _desc;

    protected BaseMarshaller(Descriptor desc) {
        _desc = desc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Descriptor getDescriptor() {
        return _desc;
    }

    /**
     * By default, will throw an {@link java.lang.UnsupportedOperationException}.
     * @param config the config
     * @return the new Model
     */
    @Override
    public Model read(Configuration config) {
        throw new UnsupportedOperationException();
    }

    /**
     * By default, will delegate the writing to the specified Model.
     * @param model the Model to write
     * @param out the OutputStream to write to
     * @throws IOException if a problem occurred
     */
    @Override
    public void write(Model model, OutputStream out) throws IOException {
        model.write(out);
    }

    /**
     * By default, will delegate the writing to the specified Model.
     * @param model the Model to write
     * @param writer the Writer to write to
     * @throws IOException if a problem occurred
     */
    @Override
    public void write(Model model, Writer writer) throws IOException {
        model.write(writer);
    }

}
