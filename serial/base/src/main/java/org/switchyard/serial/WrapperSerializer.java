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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A serializer that wraps another serializer.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class WrapperSerializer extends BaseSerializer {

    private final Serializer _wrapped;

    /**
     * Constructor with a serializer to wrap.
     * @param serializer the serializer to wrap
     */
    public WrapperSerializer(Serializer serializer) {
        super(serializer.getFormat(), serializer.getCompression());
        _wrapped = serializer;
    }

    /**
     * Constructor with a serializer and compression.
     * @param serializer the serializer
     * @param compression the compression
     */
    public WrapperSerializer(Serializer serializer, CompressionType compression) {
        super(serializer.getFormat(), compression);
        _wrapped = serializer;
    }

    /**
     * Gets the wrapped serializer.
     * @return the wrapped serializer
     */
    public Serializer getWrapped() {
        return _wrapped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out) throws IOException {
        return getWrapped().serialize(obj, type, out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream in, Class<T> type) throws IOException {
        return getWrapped().deserialize(in, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBufferSize() {
        return getWrapped().getBufferSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializer setBufferSize(int bufferSize) {
        getWrapped().setBufferSize(bufferSize);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrettyPrint() {
        return getWrapped().isPrettyPrint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializer setPrettyPrint(boolean prettyPrint) {
        getWrapped().setPrettyPrint(prettyPrint);
        return this;
    }

}
