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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.switchyard.serial.FormatType;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;

/**
 * A Protostuff serializer that performs {@link FormatType.NATIVE_PROTOSTUFF} serialization/deserialization.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class NativeProtostuffSerializer extends BaseProtostuffSerializer {

    /**
     * Default constructor.
     */
    public NativeProtostuffSerializer() {
        super(FormatType.NATIVE_PROTOSTUFF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void writeTo(OutputStream out, T obj, Schema<T> schema) throws IOException {
        try {
            ProtostuffIOUtil.writeTo(out, obj, schema, LinkedBuffer.allocate(getBufferSize()));
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
            ProtostuffIOUtil.mergeFrom(in, obj, schema, LinkedBuffer.allocate(getBufferSize()));
        } finally {
            if (isCloseEnabled()) {
                in.close();
            }
        }
    }

}
