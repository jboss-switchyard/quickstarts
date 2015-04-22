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
package org.switchyard.common.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An OutputStream that keeps track of the number of bytes written to it.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class CountingOutputStream extends OutputStream {

    private final OutputStream _out;
    private int _count;

    /**
     * Wraps a given OutputSteam.
     * @param out the wrapped OutputStream
     */
    public CountingOutputStream(OutputStream out) {
        _out = out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b) throws IOException {
        _out.write(b);
        _count += b.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        _out.write(b, off, len);
        _count += len;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException {
        _out.write(b);
        _count++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        _out.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        _out.close();
    }

    /**
     * Retrieves the number of bytes written.
     * @return the number of bytes written
     */
    public int getCount() {
        return _count;
    }

}
