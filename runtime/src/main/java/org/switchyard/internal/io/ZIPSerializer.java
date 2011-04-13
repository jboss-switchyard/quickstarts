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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.switchyard.common.io.CountingOutputStream;

/**
 * de/serializes objects using ZIP compression.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class ZIPSerializer extends BaseSerializer {

    private final Serializer _serializer;

    /**
     * Constructs a new ZIPCompressionSerializer with the specified wrapped serializer.
     * @param serializer the wrapped serializer
     */
    public ZIPSerializer(Serializer serializer) {
        _serializer = serializer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out, int bufferSize) throws IOException {
        out = new CountingOutputStream(out);
        ZipOutputStream zip = new ZipOutputStream(out);
        try {
            zip.putNextEntry(new ZipEntry("z"));
            _serializer.serialize(obj, type, zip, bufferSize);
            zip.closeEntry();
            zip.finish();
            zip.flush();
        } finally {
            if (isCloseEnabled()) {
                zip.close();
            }
        }
        return ((CountingOutputStream)out).getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream in, Class<T> type, int bufferSize) throws IOException {
        ZipInputStream zip = new ZipInputStream(in);
        try {
            zip.getNextEntry();
            return _serializer.deserialize(zip, type, bufferSize);
        } finally {
            if (isCloseEnabled()) {
                zip.close();
            }
        }
    }

}
