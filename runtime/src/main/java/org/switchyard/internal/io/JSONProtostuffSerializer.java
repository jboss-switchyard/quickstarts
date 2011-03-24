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
package org.switchyard.internal.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.switchyard.io.CountingOutputStream;

import com.dyuproject.protostuff.JsonIOUtil;
import com.dyuproject.protostuff.Schema;

/**
 * Uses the Protostuff library to do JSON de/serialization.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class JSONProtostuffSerializer extends BaseProtostuffSerializer {

    private boolean _numeric;

    /**
     * Calls JSONProtostuffSerializer(false).
     */
    public JSONProtostuffSerializer() {
        this(false);
    }

    /**
     * Constructor used to define whether or not to use numeric JSON serialization.
     * @param numeric whether or not to use numeric JSON serialization
     */
    public JSONProtostuffSerializer(boolean numeric) {
        _numeric = numeric;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int writeTo(OutputStream out, T obj, Schema<T> schema, int bufferSize) throws IOException {
        out = new CountingOutputStream(new BufferedOutputStream(out, bufferSize));
        try {
            JsonIOUtil.writeTo(out, obj, schema, _numeric);
            out.flush();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Throwable t) {
                // minimum one statement just to keep checkstyle happy
                t.getMessage();
            }
        }
        return ((CountingOutputStream)out).getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void mergeFrom(InputStream in, T obj, Schema<T> schema, int bufferSize) throws IOException {
        in = new BufferedInputStream(in, bufferSize);
        try {
            JsonIOUtil.mergeFrom(in, obj, schema, _numeric);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Throwable t) {
                // minimum one statement just to keep checkstyle happy
                t.getMessage();
            }
        }
    }

}
