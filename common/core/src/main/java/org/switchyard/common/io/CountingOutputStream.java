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
