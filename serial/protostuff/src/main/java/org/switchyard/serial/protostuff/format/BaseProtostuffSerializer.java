/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.serial.protostuff.format;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.switchyard.common.io.CountingOutputStream;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.serial.BaseSerializer;
import org.switchyard.serial.FormatType;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * Base Protostuff serializer.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class BaseProtostuffSerializer extends BaseSerializer {

    /**
     * Constructor with a format.
     * @param format the format
     */
    protected BaseProtostuffSerializer(FormatType format) {
        super(format);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out) throws IOException {
        out = new CountingOutputStream(new BufferedOutputStream(out, getBufferSize()));
        Schema<T> schema = RuntimeSchema.getSchema(type);
        writeTo(out, obj, schema);
        return ((CountingOutputStream)out).getCount();
    }

    /**
     * Protostuff serialization mechanism.
     * @param <T> the type of object
     * @param out the output stream
     * @param obj the object
     * @param schema the schema
     * @throws IOException something wicked this way comes
     */
    public abstract <T> void writeTo(OutputStream out, T obj, Schema<T> schema) throws IOException;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream in, Class<T> type) throws IOException {
        in = new BufferedInputStream(in, getBufferSize());
        T obj = Construction.construct(type);
        Schema<T> schema = RuntimeSchema.getSchema(type);
        mergeFrom(in, obj, schema);
        return obj;
    }

    /**
     * Protostuff deserialization mechanism.
     * @param <T> the type of object
     * @param in the input stream
     * @param obj the object
     * @param schema the schema
     * @throws IOException something wicked this way comes
     */
    public abstract <T> void mergeFrom(InputStream in, T obj, Schema<T> schema) throws IOException;

}
