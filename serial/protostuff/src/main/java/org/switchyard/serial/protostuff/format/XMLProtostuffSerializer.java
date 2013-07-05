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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.switchyard.serial.FormatType;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.XmlIOUtil;

/**
 * A Protostuff serializer that performs {@link FormatType.XML_PROTOSTUFF} serialization/deserialization.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class XMLProtostuffSerializer extends BaseProtostuffSerializer {

    /**
     * Default constructor.
     */
    public XMLProtostuffSerializer() {
        super(FormatType.XML_PROTOSTUFF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void writeTo(OutputStream out, T obj, Schema<T> schema) throws IOException {
        try {
            XmlIOUtil.writeTo(out, obj, schema);
            out.flush();
        } finally {
            if (isCloseEnabled()) {
                out.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void mergeFrom(InputStream in, T obj, Schema<T> schema) throws IOException {
        try {
            XmlIOUtil.mergeFrom(in, obj, schema);
        } finally {
            if (isCloseEnabled()) {
                in.close();
            }
        }
    }

}
