/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.common.io.resource;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.type.Classes;

/**
 * Represents the type of a Resource.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class ResourceType {

    private static final Logger LOGGER = Logger.getLogger(ResourceType.class);

    private static final Map<String,ResourceType> TYPES = new ConcurrentHashMap<String,ResourceType>();

    static {
        List<URL> urls;
        try {
            urls = Classes.getResources("/org/switchyard/common/io/resource/resourceType.properties", ResourceType.class);
        } catch (Throwable t) {
            LOGGER.fatal(t.getMessage());
            urls = Collections.emptyList();
        }
        PropertiesPuller props_puller = new PropertiesPuller();
        for (URL url : urls) {
            try {
                Properties props = props_puller.pull(url);
                for (Object key : props.keySet()) {
                    String name = (String)key;
                    String value = props.getProperty(name);
                    StringTokenizer desc_exts = new StringTokenizer(value, "|");
                    String description = desc_exts.hasMoreTokens() ? desc_exts.nextToken() : null;
                    Set<String> ext_set = new LinkedHashSet<String>();
                    if (desc_exts.hasMoreTokens()) {
                        StringTokenizer exts = new StringTokenizer(desc_exts.nextToken(), ",");
                        while (exts.hasMoreTokens()) {
                            ext_set.add(exts.nextToken());
                        }
                    }
                    install(name, description, ext_set);
                }
            } catch (Throwable t) {
                LOGGER.error(t.getMessage());
            }
        }
    }

    private final String _name;
    private String _description; // not final on purpose; see install
    private final Set<String> _extensions;

    private ResourceType(String name, String description, Set<String> extensions) {
        _name = name;
        _description = description;
        _extensions = extensions;
    }

    /**
     * The name of the resource type.
     * @return the name of the resource type
     */
    public String getName() {
        return _name;
    }

    /**
     * The description of the resource type.
     * @return the description of the resource type
     */
    public String getDescription() {
        return _description;
    }

    /**
     * The extensions of the resource type.
     * @return the extensions of the resource type
     */
    public Set<String> getExtensions() {
        return Collections.unmodifiableSet(_extensions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ResourceType other = (ResourceType)obj;
        if (_name == null) {
            if (other._name != null) {
                return false;
            }
        } else if (!_name.equals(other._name)) {
            return false;
        }
        return true;
    }

    /**
     * Installs a resource type with the specified name.
     * @param name the name of the resource type
     * @return the installed resource type
     */
    public static synchronized ResourceType install(String name) {
        return install(name, null);
    }

    /**
     * Installs a resource type with the specified name, description, and extensions.
     * @param name the name of the resource type
     * @param description the description of the resource type
     * @param extensions the extensions of the resource type
     * @return the installed resource type
     */
    public static synchronized ResourceType install(String name, String description, String... extensions) {
        Set<String> ext_set = null;
        if (extensions != null) {
            for (String ext : extensions) {
                if (ext != null) {
                    if (ext_set == null) {
                        ext_set = new LinkedHashSet<String>();
                    }
                    ext_set.add(ext);
                }
            }
        }
        return install(name, description, ext_set);
    }

    /**
     * Installs a resource type with the specified name, description, and extensions.
     * @param name the name of the resource type
     * @param description the description of the resource type
     * @param extensions the extensions of the resource type
     * @return the installed resource type
     */
    public static synchronized ResourceType install(String name, String description, Set<String> extensions) {
        // name
        if (name == null) {
            throw new IllegalArgumentException("name == null");
        }
        name = name.trim();
        if (name.length() == 0) {
            throw new IllegalArgumentException("name.trim().length() == 0");
        }
        name = name.toUpperCase();
        // description
        if (description != null) {
            description = description.trim();
        }
        // extensions
        Set<String> ext_set = new LinkedHashSet<String>();
        if (extensions != null) {
            for (String ext : extensions) {
                if (ext != null) {
                    ext = ext.trim();
                    if (ext.length() > 0) {
                        ext = ext.toLowerCase();
                        ext_set.add(ext);
                    }
                }
            }
        }
        // type
        ResourceType type = TYPES.get(name);
        if (type == null) {
            type = new ResourceType(name, description, ext_set);
            TYPES.put(name, type);
        } else {
            if (type._description == null && description != null) {
                type._description = description;
            }
            for (String ext : ext_set) {
                if (!type._extensions.contains(ext)) {
                    type._extensions.add(ext);
                }
            }
        }
        return type;
    }

    /**
     * Gets the installed resource type with the specified name.
     * @param name the specified name
     * @return the installed resource type
     */
    public static ResourceType valueOf(String name) {
        return name != null ? TYPES.get(name.trim().toUpperCase()) : null;
    }

}
