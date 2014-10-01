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

package org.switchyard.common.io.pull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.switchyard.common.CommonCoreMessages;


/**
 * Utility class to safely access ("pull") Properties from various sources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class PropertiesPuller extends Puller<Properties> {

    /**
     * The type of the Properties file to pull.
     */
    public static enum PropertiesType {
        /** A standard properties file. */
        PROPERTIES,
        /** An XML properties file. */
        XML
    };

    private final PropertiesType _type;

    /**
     * Creates a new PropertiesPuller with type {@link PropertiesType#PROPERTIES}.
     */
    public PropertiesPuller() {
        this(PropertiesType.PROPERTIES);
    }

    /**
     * Creates a new PropertiesPuller with the specified PropertiesType.
     * @param type the specified PropertiesType
     */
    public PropertiesPuller(PropertiesType type) {
        _type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties pull(InputStream stream) throws IOException {
        Properties props = new Properties();
        if (stream != null) {
            switch (_type) {
                case PROPERTIES:
                    props.load(stream);
                    break;
                case XML:
                    props.loadFromXML(stream);
                    break;
                default:
                    throw CommonCoreMessages.MESSAGES.unsupportedPropertiesTypeForMethod(_type, "pull(InputStream)");
            }
        }
        return props;
    }

    /**
     * Safely pulls Properties from a Reader.
     * @param reader a Reader of the resource
     * @return the resource
     * @throws IOException if a problem occurred
     */
    public Properties pull(Reader reader) throws IOException {
        Properties props = new Properties();
        if (reader != null) {
            switch (_type) {
                case PROPERTIES:
                    props.load(reader);
                    break;
                /*
                case XML:
                    props.loadFromXML(reader); // unsupported by JDK
                    break;
                */
                default:
                    throw CommonCoreMessages.MESSAGES.unsupportedPropertiesTypeForMethod(_type, "pull(Reader)");
            }
        }
        return props;
    }

    /**
     * Safely pulls Properties resource from a Map.
     * @param map a Map of the properties
     * @return the resource
     */
    public Properties pull(Map<String, String> map) {
        Properties props = new Properties();
        if (map != null) {
            for (Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null) {
                    props.setProperty(key, value);
                }
            }
        }
        return props;
    }

}
