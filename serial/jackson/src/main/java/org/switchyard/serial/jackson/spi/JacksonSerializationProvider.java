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
package org.switchyard.serial.jackson.spi;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.switchyard.common.type.reflect.Construction;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.jackson.format.JSONJacksonSerializer;
import org.switchyard.serial.spi.SerializationProvider;

/**
 * Jackson serialization provider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class JacksonSerializationProvider extends SerializationProvider {

    private static final Map<FormatType, Class<? extends Serializer>> MAP;
    static {
        Map<FormatType, Class<? extends Serializer>> map = new LinkedHashMap<FormatType, Class<? extends Serializer>>();
        map.put(FormatType.JSON, JSONJacksonSerializer.class);
        //map.put(FormatType.JSON_NUMERIC, NumericJSONJacksonSerializer.class);
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
