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
package org.switchyard.internal.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.switchyard.common.io.CountingOutputStream;
import org.switchyard.common.type.reflect.Construction;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * Base class for de/serializes objects using the Protostuff library.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseProtostuffSerializer extends BaseSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out, int bufferSize) throws IOException {
        out = new CountingOutputStream(new BufferedOutputStream(out, bufferSize));
        Schema<T> schema = RuntimeSchema.getSchema(type);
        writeTo(out, obj, schema, bufferSize);
        return ((CountingOutputStream)out).getCount();
    }

    /**
     * Intended for specific Protostuff mechanisms to override for serialization.
     * @param <T> the type of object
     * @param out the OutputStream to write to
     * @param obj the object to write
     * @param schema the object class runtime schema
     * @param bufferSize the buffer size to use
     * @throws IOException if a problem ocurrs during serialization
     */
    public abstract <T> void writeTo(OutputStream out, T obj, Schema<T> schema, int bufferSize) throws IOException;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream in, Class<T> type, int bufferSize) throws IOException {
        in = new BufferedInputStream(in, bufferSize);
        T obj = Construction.construct(type);
        Schema<T> schema = RuntimeSchema.getSchema(type);
        mergeFrom(in, obj, schema, bufferSize);
        return obj;
    }

    /**
     * Intended for specific Protostuff mechanisms to override for deserialization.
     * @param <T> the type of object
     * @param in the InputStream to read from
     * @param obj the object to merge into
     * @param schema the object class runtime schema
     * @param bufferSize the buffer size to use
     * @throws IOException if a problem ocurrs during deserialization
     */
    public abstract <T> void mergeFrom(InputStream in, T obj, Schema<T> schema, int bufferSize) throws IOException;

}
