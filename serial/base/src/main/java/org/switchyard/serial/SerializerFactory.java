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
package org.switchyard.serial;

import org.switchyard.serial.compress.GZIPSerializer;
import org.switchyard.serial.compress.ZIPSerializer;
import org.switchyard.serial.map.MapperSerializer;
import org.switchyard.serial.spi.SerializationProvider;

/**
 * Creates serializers.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class SerializerFactory {

    /**
     * Creates a serializer of the default format ({@link FormatType.SER_OBJECT}) and no compression.
     * @return the serializer
     */
    public static final Serializer create() {
        return create(FormatType.SER_OBJECT);
    }

    /**
     * Creates a serializer of the specified format and no compression.
     * @param format the specified format
     * @return the serializer
     */
    public static final Serializer create(FormatType format) {
        return create(format, null);
    }

    /**
     * Creates a serializer of the specified format and compression.
     * @param format the format
     * @param compression the compression
     * @return the serializer
     */
    public static final Serializer create(FormatType format, CompressionType compression) {
        return create(format, compression, false);
    }

    /**
     * Creates a serializer of the specified format, compression, and mapping capability.
     * @param format the format
     * @param compression the compression
     * @param map if mapping should be enabled
     * @return the serializer
     */
    public static final Serializer create(FormatType format, CompressionType compression, boolean map) {
        Serializer serializer = null;
        SerializationProvider provider = SerializationProvider.getPrimaryProvider(format);
        if (provider != null) {
            serializer = provider.newSerializer(format);
            if (map) {
                serializer = new MapperSerializer(serializer);
            }
            if (compression != null) {
                if (CompressionType.GZIP.equals(compression)) {
                    serializer = new GZIPSerializer(serializer);
                } else if (CompressionType.ZIP.equals(compression)) {
                    serializer = new ZIPSerializer(serializer);
                }
            }
        }
        return serializer;
    }

    private SerializerFactory() {}

}
