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
package org.switchyard.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.activation.DataSource;

/**
 * An thread-safe, in-memory DataSource for testing purposes.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class TestDataSource implements DataSource, Serializable {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private String _name;
    private String _contentType;
    private byte[] _data;
    private final ReadWriteLock _dataLock = new ReentrantReadWriteLock();

    /**
     * Constructs a new TestDataSource.
     */
    public TestDataSource() {}

    /**
     * Constructs a new TestDataSource.
     * @param name the name
     */
    public TestDataSource(String name) {
        setName(name);
    }

    /**
     * Constructs a new TestDataSource.
     * @param name the name
     * @param contentType the contentType
     */
    public TestDataSource(String name, String contentType) {
        setName(name).setContentType(contentType);
    }

    /**
     * Constructs a new TestDataSource.
     * @param name the name
     * @param contentType the contentType
     * @param data the data
     */
    public TestDataSource(String name, String contentType, byte[] data) {
        setName(name).setContentType(contentType).setData(data);
    }

    /**
     * Constructs a new TestDataSource.
     * @param name the name
     * @param contentType the contentType
     * @param data the data as a String
     */
    public TestDataSource(String name, String contentType, String data) {
        setName(name).setContentType(contentType).setDataString(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * Sets the name.
     * @param name the name
     * @return this TestDataSource (useful for chaining)
     */
    public TestDataSource setName(String name) {
        _name = name;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType() {
        return _contentType;
    }

    /**
     * Sets the contentType.
     * @param contentType the contentType
     * @return this TestDataSource (useful for chaining)
     */
    public TestDataSource setContentType(String contentType) {
        _contentType = contentType;
        return this;
    }

    /**
     * Gets the data.
     * @return the data
     */
    public byte[] getData() {
        byte[] copy;
        _dataLock.readLock().lock();
        try {
            copy = new byte[_data.length];
            System.arraycopy(_data, 0, copy, 0, _data.length);
        } finally {
            _dataLock.readLock().unlock();
        }
        return copy;
    }

    /**
     * Sets the data.
     * @param data the data
     * @return this TestDataSource (useful for chaining)
     */
    public TestDataSource setData(byte[] data) {
        if (data == null) {
            data = new byte[0];
        }
        byte[] copy = new byte[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        _dataLock.writeLock().lock();
        try {
            _data = copy;
        } finally {
            _dataLock.writeLock().unlock();
        }
        return this;
    }

    /**
     * Gets the data as a String.
     * @return the data as a String
     */
    public String getDataString() {
        return new String(getData(), UTF8);
    }

    /**
     * Sets the data as a String.
     * @param data the data as a String
     * @return this TestDataSource (useful for chaining)
     */
    public TestDataSource setDataString(String data) {
        setData(data != null ? data.getBytes(UTF8) : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(getData());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return new ByteArrayOutputStream() {
            @Override
            public void flush() throws IOException {
                super.flush();
                setData(toByteArray());
            }
            @Override
            public void close() throws IOException {
                super.close();
                setData(toByteArray());
            }
        };
    }

}
