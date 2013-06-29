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
public enum Element {

    /**
     * unknown element.
     */
    UNKNOWN(null),

    /**
     * socket-binding element.
     */
    SOCKET_BINDING("socket-binding"),

    /**
     * modules element.
     */
    MODULES("modules"),

    /**
     * module element.
     */
    MODULE("module"),

    /**
     * modules element.
     */
    EXTENSIONS("extensions"),

    /**
     * module element.
     */
    EXTENSION("extension"),

    /**
     * properties element.
     */
    PROPERTIES("properties");

    private final String _name;

    Element(final String name) {
        _name = name;
    }

    /**
     * Get the local name of this element.
     *
     * @return the local name
     */
    public String getLocalName() {
        return _name;
    }

    private static final Map<String, Element> MAP;

    static {
        final Map<String, Element> map = new HashMap<String, Element>();
        for (Element element : values()) {
            final String name = element.getLocalName();
            if (name != null) {
                map.put(name, element);
            }
        }
        MAP = map;
    }

    /**
     * Returns the Element for a given local name.
     * 
     * @param localName the local name
     * @return the Element
     */
    public static Element forName(String localName) {
        final Element element = MAP.get(localName);
        return element == null ? UNKNOWN : element;
    }

    /**
     * Return the string representation of this Element.
     * 
     * @return the string form
     */
    public String toString() {
        return getLocalName();
    }
}
