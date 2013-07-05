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
package org.switchyard.serial.compress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.switchyard.common.io.CountingOutputStream;
import org.switchyard.serial.CompressionType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.WrapperSerializer;

/**
 * A wrapper serializer that performs ZIP compression/decompression.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class ZIPSerializer extends WrapperSerializer {

    /**
     * Constructor with a serializer to wrap.
     * @param serializer the serializer to wrap
     */
    public ZIPSerializer(Serializer serializer) {
        super(serializer, CompressionType.ZIP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out) throws IOException {
        out = new CountingOutputStream(out);
        ZipOutputStream zip = new ZipOutputStream(out);
        try {
            zip.putNextEntry(new ZipEntry("z"));
            getWrapped().serialize(obj, type, zip);
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
    public <T> T deserialize(InputStream in, Class<T> type) throws IOException {
        ZipInputStream zip = new ZipInputStream(in);
        try {
            zip.getNextEntry();
            return getWrapped().deserialize(zip, type);
        } finally {
            if (isCloseEnabled()) {
                zip.close();
            }
        }
    }

}
