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
package org.switchyard.serial.jackson.format;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.switchyard.common.io.CountingOutputStream;
import org.switchyard.serial.BaseSerializer;
import org.switchyard.serial.FormatType;

/**
 * A Jackson serializer that performs {@link FormatType.JSON} serialization/deserialization.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class JSONJacksonSerializer extends BaseSerializer {

    private static final ObjectMapper OBJECT_MAPPER;
    static {
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        jsonFactory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        OBJECT_MAPPER = new ObjectMapper(jsonFactory);
        OBJECT_MAPPER.enableDefaultTyping();
        OBJECT_MAPPER.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
    }

    /**
     * Default constructor.
     */
    public JSONJacksonSerializer() {
        super(FormatType.JSON);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out) throws IOException {
        out = new CountingOutputStream(new BufferedOutputStream(out, getBufferSize()));
        try {
            ObjectWriter writer = OBJECT_MAPPER.writerWithType(type);
            if (isPrettyPrint()) {
                writer = writer.withDefaultPrettyPrinter();
            }
            writer.writeValue(out, obj);
        } finally {
            if (isCloseEnabled()) {
                out.close();
            }
        }
        return ((CountingOutputStream)out).getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream in, Class<T> type) throws IOException {
        T obj;
        try {
            obj = OBJECT_MAPPER.readValue(in, type);
        } finally {
            if (isCloseEnabled()) {
                in.close();
            }
        }
        return obj;
    }

}
