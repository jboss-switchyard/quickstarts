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
package org.switchyard.serial.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.switchyard.common.type.Classes;
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
        Iterable<SerializationProvider> providers = ServiceLoader.load(SerializationProvider.class, Classes.getClassLoader(SerializationProvider.class));
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
