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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.switchyard.common.type.Classes;
import org.switchyard.common.util.ProviderRegistry;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;

/**
 * Provides serializers for supported formats.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class SerializationProvider {

    private static final Map<FormatType, List<SerializationProvider>> MAP;
    static {
        Map<FormatType, List<SerializationProvider>> map = new LinkedHashMap<FormatType, List<SerializationProvider>>();
        Iterable<SerializationProvider> providers = ProviderRegistry.getProviders(SerializationProvider.class, Classes.getClassLoader(SerializationProvider.class));
        for (SerializationProvider provider : providers) {
            for (FormatType format : provider.getSupportedFormats()) {
                List<SerializationProvider> list = map.get(format);
                if (list == null) {
                    list = new ArrayList<SerializationProvider>();
                    map.put(format, list);
                }
                list.add(provider);
            }
        }
        for (FormatType format : new LinkedHashSet<FormatType>(map.keySet())) {
            map.put(format, Collections.unmodifiableList(map.remove(format)));
        }
        MAP = Collections.unmodifiableMap(map);
    }

    /**
     * Gets the supported formats.
     * @return the supported formats
     */
    public abstract Set<FormatType> getSupportedFormats();

    /**
     * Creates a new serializer for a format.
     * @param format the format
     * @return the serializer
     */
    public abstract Serializer newSerializer(FormatType format);

    /**
     * Gets all providers that support a format.
     * @param format the format
     * @return the providers
     */
    public static final List<SerializationProvider> getProviders(FormatType format) {
        return MAP.containsKey(format) ? MAP.get(format) : Collections.<SerializationProvider>emptyList();
    }

    /**
     * Gets the primary provider that supports a format.
     * @param format the format
     * @return the primary provider
     */
    public static final SerializationProvider getPrimaryProvider(FormatType format) {
        Iterator<SerializationProvider> providers = getProviders(format).iterator();
        return providers.hasNext() ? providers.next() : null;
    }

}
