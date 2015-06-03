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

    private final ObjectMapper _objectMapper;

    /**
     * Default constructor.
     */
    public JSONJacksonSerializer() {
        super(FormatType.JSON);
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        jsonFactory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        _objectMapper = new ObjectMapper(jsonFactory);
        _objectMapper.enableDefaultTyping();
        _objectMapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out) throws IOException {
        out = new CountingOutputStream(new BufferedOutputStream(out, getBufferSize()));
        try {
            ObjectWriter writer = _objectMapper.writerWithType(type);
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
            obj = _objectMapper.readValue(in, type);
        } finally {
            if (isCloseEnabled()) {
                in.close();
            }
        }
        return obj;
    }

}
