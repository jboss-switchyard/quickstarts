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
package org.switchyard.internal.io.graph;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.switchyard.internal.io.Serializer;

/**
 * A Graph representing an InputStream, internalized as an array of bytes.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class InputStreamGraph implements Graph<InputStream> {

    private byte[] _bytes;

    /**
     * Gets the wrapped bytes.
     * @return the wrapped bytes
     */
    public byte[] getBytes() {
        return _bytes;
    }

    /**
     * Sets the wrapped bytes.
     * @param bytes the bytes to wrap
     */
    public void setBytes(byte[] bytes) {
        _bytes = bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(InputStream object, Map<Integer,Object> visited) throws IOException {
        int bs = Serializer.DEFAULT_BUFFER_SIZE;
        BufferedInputStream bis = new BufferedInputStream(object, bs);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bs);
        BufferedOutputStream bos = new BufferedOutputStream(baos, bs);
        byte[] buff = new byte[bs];
        int read = 0;
        while ((read = bis.read(buff)) != -1) {
            bos.write(buff, 0, read);
        }
        bos.flush();
        setBytes(baos.toByteArray());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream decompose(Map<Integer,Object> visited) throws IOException {
        return new ByteArrayInputStream(getBytes());
    }

    @Override
    public String toString() {
        byte[] bytes = getBytes();
        return "InputStreamGraph(bytes.length=" + (bytes != null ? bytes.length : "null") + ")";
    }

}
