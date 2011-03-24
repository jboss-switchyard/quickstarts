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
package org.switchyard.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implements most of the overloaded methods, leaving just one for serialization and one for deserialization for subclasses.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseSerializer implements Serializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> byte[] serialize(T obj, Class<T> type) throws IOException {
        return serialize(obj, type, DEFAULT_BUFFER_SIZE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> byte[] serialize(T obj, Class<T> type, int bufferSize) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int count = serialize(obj, type, out, bufferSize);
        byte[] bytes = out.toByteArray();
        assert count == bytes.length;
        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out) throws IOException {
        return serialize(obj, type, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        return deserialize(bytes, type, DEFAULT_BUFFER_SIZE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type, int bufferSize) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        return deserialize(in, type, bufferSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream in, Class<T> type) throws IOException {
        return deserialize(in, type, DEFAULT_BUFFER_SIZE);
    }

}
