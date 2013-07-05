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
