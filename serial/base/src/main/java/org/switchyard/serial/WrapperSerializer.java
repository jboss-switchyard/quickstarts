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
