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
package org.switchyard.serial.protostuff.spi;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.switchyard.common.type.reflect.Construction;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.protostuff.format.GraphProtostuffSerializer;
import org.switchyard.serial.protostuff.format.JSONProtostuffSerializer;
import org.switchyard.serial.protostuff.format.NativeProtostuffSerializer;
import org.switchyard.serial.protostuff.format.NumericJSONProtostuffSerializer;
import org.switchyard.serial.protostuff.format.ProtobufProtostuffSerializer;
import org.switchyard.serial.protostuff.format.XMLProtostuffSerializer;
import org.switchyard.serial.spi.SerializationProvider;

/**
 * Protostuff serialization provider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class ProtostuffSerializationProvider extends SerializationProvider {

    private static final Map<FormatType, Class<? extends Serializer>> MAP;
    static {
        Map<FormatType, Class<? extends Serializer>> map = new LinkedHashMap<FormatType, Class<? extends Serializer>>();
        map.put(FormatType.GRAPH_PROTOSTUFF, GraphProtostuffSerializer.class);
        map.put(FormatType.JSON, JSONProtostuffSerializer.class);
        map.put(FormatType.JSON_NUMERIC, NumericJSONProtostuffSerializer.class);
        map.put(FormatType.NATIVE_PROTOSTUFF, NativeProtostuffSerializer.class);
        map.put(FormatType.PROTOBUF, ProtobufProtostuffSerializer.class);
        map.put(FormatType.XML_PROTOSTUFF, XMLProtostuffSerializer.class);
        MAP = Collections.unmodifiableMap(map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FormatType> getSupportedFormats() {
        return MAP.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializer newSerializer(FormatType format) {
        Class<? extends Serializer> c = MAP.get(format);
        return c != null ? Construction.construct(c) : null;
    }

}
