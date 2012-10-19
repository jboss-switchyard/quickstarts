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
package org.switchyard.serial.graph.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.switchyard.common.codec.Base64;
import org.switchyard.common.io.Buffers;
import org.switchyard.serial.graph.Graph;

/**
 * A node representing an InputStream.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class InputStreamNode implements Node {

    private String _base64;

    /**
     * Default constructor.
     */
    public InputStreamNode() {}

    /**
     * Gets the Base64 representation.
     * @return the Base64 representation
     */
    public String getBase64() {
        return _base64;
    }

    /**
     * Sets the Base64 representation.
     * @param base64 the Base64 representation
     */
    public void setBase64(String base64) {
        _base64 = base64;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Object obj, Graph graph) {
        int bs = Buffers.DEFAULT_SIZE;
        BufferedInputStream bis = new BufferedInputStream((InputStream)obj, bs);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bs);
        BufferedOutputStream bos = new BufferedOutputStream(baos, bs);
        byte[] buff = new byte[bs];
        int read = 0;
        try {
            while ((read = bis.read(buff)) != -1) {
                bos.write(buff, 0, read);
            }
            bos.flush();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        setBase64(Base64.encode(baos.toByteArray()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object decompose(Graph graph) {
        return new ByteArrayInputStream(Base64.decode(getBase64()));
    }

}
