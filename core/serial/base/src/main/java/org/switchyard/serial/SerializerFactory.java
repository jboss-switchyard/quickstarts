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
package org.switchyard.serial;

import org.switchyard.serial.compress.GZIPSerializer;
import org.switchyard.serial.compress.ZIPSerializer;
import org.switchyard.serial.graph.GraphSerializer;
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
     * Creates a serializer of the specified format, compression, and graph enabling.
     * @param format the format
     * @param compression the compression
     * @param graph if graphing should be enabled
     * @return the serializer
     */
    public static final Serializer create(FormatType format, CompressionType compression, boolean graph) {
        Serializer serializer = null;
        SerializationProvider provider = SerializationProvider.getPrimaryProvider(format);
        if (provider != null) {
            serializer = provider.newSerializer(format);
            if (graph) {
                serializer = new GraphSerializer(serializer);
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
