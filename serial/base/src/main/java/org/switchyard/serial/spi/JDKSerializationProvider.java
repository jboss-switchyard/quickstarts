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
package org.switchyard.serial.spi;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.switchyard.common.type.reflect.Construction;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.format.SERObjectSerializer;
import org.switchyard.serial.format.XMLBeanSerializer;

/**
 * JDK built-in serialization provider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class JDKSerializationProvider extends SerializationProvider {

    private static final Map<FormatType, Class<? extends Serializer>> MAP;
    static {
        Map<FormatType, Class<? extends Serializer>> map = new LinkedHashMap<FormatType, Class<? extends Serializer>>();
        map.put(FormatType.SER_OBJECT, SERObjectSerializer.class);
        map.put(FormatType.XML_BEAN, XMLBeanSerializer.class);
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
