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
package org.switchyard.as7.extension;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 *
 */
public enum Attribute {
    /**
     * unknown attribute.
     */
    UNKNOWN(null),

    /**
     * identifier attribute.
     */
    IDENTIFIER("identifier"),

    /**
     * implClass attribute.
     */
    IMPLCLASS("implClass"),

    /**
     * names attribute.
     */
    NAMES("names");

    private final String _name;

    Attribute(final String name) {
        _name = name;
    }

    /**
     * Get the local name of this attribute.
     *
     * @return the local name
     */
    public String getLocalName() {
        return _name;
    }

    private static final Map<String, Attribute> MAP;

    static {
        final Map<String, Attribute> map = new HashMap<String, Attribute>();
        for (Attribute element : values()) {
            final String name = element.getLocalName();
            if (name != null) {
                map.put(name, element);
            }
        }
        MAP = map;
    }

    /**
     * Returns the Attribute for a given local name.
     * 
     * @param localName the local name
     * @return the Attribute
     */
    public static Attribute forName(String localName) {
        final Attribute element = MAP.get(localName);
        return element == null ? UNKNOWN : element;
    }

    /**
     * Return the string representation of this Attribute.
     * 
     * @return the string form
     */
    public String toString() {
        return getLocalName();
    }
}
