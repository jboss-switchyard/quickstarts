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
package org.switchyard.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.switchyard.common.io.Buffers;

/**
 * Base serializer implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class BaseSerializer implements Serializer {

    private final FormatType _format;
    private final CompressionType _compression;

    private int _bufferSize = Buffers.DEFAULT_SIZE;
    private boolean _closeEnabled = false;
    private boolean _prettyPrint = false;

    /**
     * Constructor with a format.
     * @param formatType the format.
     */
    public BaseSerializer(FormatType formatType) {
        this(formatType, null);
    }

    /**
     * Constructor with a format and compression.
     * @param format the format
     * @param compression the compression
     */
    public BaseSerializer(FormatType format, CompressionType compression) {
        _format = format;
        _compression = compression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> byte[] serialize(T obj, Class<T> type) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(getBufferSize());
        int count = serialize(obj, type, out);
        byte[] bytes = out.toByteArray();
        assert count == bytes.length;
        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        return deserialize(new ByteArrayInputStream(bytes), type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormatType getFormat() {
        return _format;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompressionType getCompression() {
        return _compression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBufferSize() {
        return _bufferSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializer setBufferSize(int bufferSize) {
        _bufferSize = bufferSize;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCloseEnabled() {
        return _closeEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializer setCloseEnabled(boolean closeEnabled) {
        _closeEnabled = closeEnabled;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrettyPrint() {
        return _prettyPrint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializer setPrettyPrint(boolean prettyPrint) {
        _prettyPrint = prettyPrint;
        return this;
    }

}
