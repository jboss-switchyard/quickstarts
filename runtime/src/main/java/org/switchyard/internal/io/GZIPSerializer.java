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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * de/serializes objects using GZIP compression.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class GZIPSerializer extends BaseSerializer {

    private final Serializer _serializer;

    /**
     * Constructs a new GZIPCompressionSerializer with the specified wrapped serializer.
     * @param serializer the wrapped serializer
     */
    public GZIPSerializer(Serializer serializer) {
        _serializer = serializer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out, int bufferSize) throws IOException {
        out = new CountingOutputStream(out);
        GZIPOutputStream gzip = new GZIPOutputStream(out, bufferSize);
        try {
            _serializer.serialize(obj, type, gzip, bufferSize);
            gzip.finish();
            gzip.flush();
        } finally {
            if (isCloseEnabled()) {
                gzip.close();
            }
        }
        return ((CountingOutputStream)out).getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream in, Class<T> type, int bufferSize) throws IOException {
        in = new GZIPInputStream(in, bufferSize);
        try {
            return _serializer.deserialize(in, type, bufferSize);
        } finally {
            if (isCloseEnabled()) {
                in.close();
            }
        }
    }

}
