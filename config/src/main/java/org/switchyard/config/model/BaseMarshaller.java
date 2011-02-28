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
