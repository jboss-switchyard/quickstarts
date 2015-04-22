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
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
