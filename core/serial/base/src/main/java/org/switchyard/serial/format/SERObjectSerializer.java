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
package org.switchyard.serial.format;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.switchyard.common.io.CountingOutputStream;
import org.switchyard.serial.BaseSerializer;
import org.switchyard.serial.FormatType;

/**
 * A JDK serializer that performs {@link FormatType.SER_OBJECT} serialization/deserialization.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class SERObjectSerializer extends BaseSerializer {

    /**
     * Default constructor.
     */
    public SERObjectSerializer() {
        super(FormatType.SER_OBJECT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out) throws IOException {
        out = new CountingOutputStream(new BufferedOutputStream(out, getBufferSize()));
        try {
            @SuppressWarnings("resource")
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(obj);
            oos.flush();
        } finally {
            if (isCloseEnabled()) {
                out.close();
            }
        }
        return ((CountingOutputStream)out).getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream in, Class<T> type) throws IOException {
        in = new BufferedInputStream(in, getBufferSize());
        try {
            ObjectInputStream ois = new ObjectInputStream(in);
            Object obj = ois.readObject();
            return type.cast(obj);
        } catch (ClassNotFoundException cnfe) {
            throw new IOException(cnfe);
        } finally {
            if (isCloseEnabled()) {
                in.close();
            }
        }
    }

}
