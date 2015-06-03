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
package org.switchyard.common.io.resource;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;
import org.switchyard.common.CommonCoreMessages;
import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.Classes;

/**
 * Represents the type of a Resource.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2011 Red Hat Inc.
 */
public final class ResourceType implements Comparable<ResourceType> {

    private static final Logger LOGGER = Logger.getLogger(ResourceType.class);

    private static final Map<String,ResourceType> TYPES = new ConcurrentHashMap<String,ResourceType>();

    static {
        install();
    }

    private final String _name;
    private String _description;
    private Set<String> _extensions;
    private Set<ResourceType> _inherited;

    private ResourceType(String name, String description, Set<String> extensions, Set<ResourceType> inherited) {
        _name = name;
        _description = description;
        _extensions = extensions;
        _inherited = inherited;
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
     * The extensions of the resource type, including inherited.
     * @return the extensions of the resource type
     */
    public Set<String> getExtensions() {
        return getExtensions(true);
    }

    /**
     * The extensions of the resource type.
     * @param includeInherited if inherited extensions should be included
     * @return the extensions of the resource type
     */
    public Set<String> getExtensions(boolean includeInherited) {
        Set<String> exts = new TreeSet<String>();
        exts.addAll(_extensions);
        if (includeInherited) {
            for (ResourceType type : _inherited) {
                exts.addAll(type.getExtensions(true));
            }
        }
        return Collections.unmodifiableSet(exts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ResourceType type) {
        return type != null ? _name.compareTo(type._name) : 1;
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
     * Searches for any /org/switchyard/common/io/resource/resourceType.properties it can find on the classpath
     * and installs the configured ResourceTypes. This method is safe to invoke multiple times, and might be
     * necessary if new ClassLoaders become available.
     */
    public static synchronized void install() {
        install(ResourceType.class);
    }

    /**
     * Searches for any /org/switchyard/common/io/resource/resourceType.properties it can find on the classpath
     * and installs the configured ResourceTypes. This method is safe to invoke multiple times, and might be
     * necessary if new ClassLoaders become available.
     * @param caller the calling Class so we can try it's ClassLoader
     */
    public static synchronized void install(Class<?> caller) {
        install(caller != null ? caller.getClassLoader() : null);
    }

    /**
     * Searches for any /org/switchyard/common/io/resource/resourceType.properties it can find on the classpath
     * and installs the configured ResourceTypes. This method is safe to invoke multiple times, and might be
     * necessary if new ClassLoaders become available.
     * @param loader a ClassLoader to try
     */
    public static synchronized void install(ClassLoader loader) {
        List<URL> urls;
        try {
            urls = Classes.getResources("/org/switchyard/common/io/resource/resourceType.properties", loader);
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
                    StringTokenizer st = new StringTokenizer(props.getProperty(name), "|");
                    String description = st.hasMoreTokens() ? Strings.trimToNull(st.nextToken()) : null;
                    Set<String> extensions = st.hasMoreTokens() ? Strings.uniqueSplitTrimToNull(st.nextToken(), ",") : null;
                    // we only want to resolve inheritance once (thus false below)
                    install(name, description, extensions, false);
                }
            } catch (Throwable t) {
                LOGGER.error(t.getMessage());
            }
        }
        // resolve inheritance once
        resolveInheritance();
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
                ext = Strings.trimToNull(ext);
                if (ext != null) {
                    if (ext_set == null) {
                        ext_set = new TreeSet<String>();
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
        return install(name, description, extensions, true);
    }

    private static synchronized ResourceType install(String name, String description, Set<String> extensions, boolean resolveInheritance) {
        // name
        name = Strings.trimToNull(name);
        if (name == null) {
            throw CommonCoreMessages.MESSAGES.nameNull();
        }
        name = name.toUpperCase();
        // description
        description = Strings.trimToNull(description);
        // extensions
        Set<String> ext_set = new TreeSet<String>();
        if (extensions != null) {
            for (String ext : extensions) {
                ext = Strings.trimToNull(ext);
                if (ext != null) {
                    ext = ext.toLowerCase();
                    ResourceType preinstalled = forExtension(ext);
                    if (preinstalled == null) {
                        ext_set.add(ext);
                    } else {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Extension [" + ext + "] already installed by [" + preinstalled._name + "], so will not be included in [" + name + "]. Use extension reference [{" + preinstalled._name + "}] instead.");
                        }
                    }
                }
            }
        }
        // type
        ResourceType type = TYPES.get(name);
        if (type == null) {
            type = new ResourceType(name, description, ext_set, new TreeSet<ResourceType>());
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
        if (resolveInheritance) {
            resolveInheritance();
        }
        return type;
    }

    private static synchronized void resolveInheritance() {
        for (ResourceType type : TYPES.values()) {
            Set<String> extensions = new TreeSet<String>();
            for (String ext : type.getExtensions(false)) {
                boolean bounded = false;
                ResourceType inherited = null;
                if (ext.startsWith("{") && ext.endsWith("}")) {
                    bounded = true;
                    String name = ext.substring(1, ext.length()-1);
                    inherited = valueOf(name);
                }
                if (inherited != null) {
                    type._inherited.add(inherited);
                } else if (!bounded) {
                    extensions.add(ext);
                }
            }
            type._extensions = extensions;
        }
    }

    /**
     * Returns all known ResourceType names as an array.
     * @return the names
     */
    public static String[] names() {
        Set<String> names = nameSet();
        return names.toArray(new String[names.size()]);
    }

    /**
     * Returns all known ResourceType names as a Set.
     * @return the names
     */
    public static Set<String> nameSet() {
        return Collections.unmodifiableSet(new TreeSet<String>(TYPES.keySet()));
    }

    /**
     * Returns all known ResourceTypes as an array.
     * @return the types
     */
    public static ResourceType[] values() {
        Set<ResourceType> values = valueSet();
        return values.toArray(new ResourceType[values.size()]);
    }

    /**
     * Returns all known ResourceTypes as a Set.
     * @return the types
     */
    public static Set<ResourceType> valueSet() {
        Set<ResourceType> tmpSet = new TreeSet<ResourceType>();
        tmpSet.addAll(TYPES.values());
        return Collections.unmodifiableSet(tmpSet);
    }

    /**
     * Gets the installed resource type with the specified name. Added to mimic Enums; simply calls {@link ResourceType#forName(String)}.
     * @param name the specified name
     * @return the installed resource type
     */
    public static ResourceType valueOf(String name) {
        return forName(name);
    }

    /**
     * Gets the installed resource type with the specified name.
     * @param name the specified name
     * @return the installed resource type
     */
    public static ResourceType forName(String name) {
        name = Strings.trimToNull(name);
        return name != null ? TYPES.get(name.toUpperCase()) : null;
    }

    /**
     * Gets the installed resource type with the specified extension in it's primary list (not inherited).
     * @param extension the specified extension
     * @return the resource type
     */
    public static ResourceType forExtension(String extension) {
        ResourceType[] types = forExtension(extension, false);
        return types.length > 0 ? types[0] : null;
    }

    /**
     * Gets all installed resource types that include the specified extension.
     * @param extension the specified extension
     * @param includeInherited if inherited extensions should be included
     * @return the resource types
     */
    public static ResourceType[] forExtension(String extension, boolean includeInherited) {
        Set<ResourceType> types = new TreeSet<ResourceType>();
        extension = Strings.trimToNull(extension);
        if (extension != null) {
            extension = extension.toLowerCase();
            for (ResourceType type : TYPES.values()) {
                if (type.getExtensions(includeInherited).contains(extension)) {
                    types.add(type);
                }
            }
        }
        return types.toArray(new ResourceType[types.size()]);
    }

    /**
     * Gets the installed resource type with an extension in it's primary list (not inherited) deduced from the specified location.
     * @param location the specified extension
     * @return the resource type
     */
    public static ResourceType forLocation(String location) {
        ResourceType[] types = forLocation(location, false);
        return types.length > 0 ? types[0] : null;
    }

    /**
     * Gets all installed resource types that include an extension in it's primary list deduced from the specified location.
     * @param location the specified extension
     * @param includeInherited if inherited extensions should be included
     * @return the resource types
     */
    public static ResourceType[] forLocation(String location, boolean includeInherited) {
        if (location != null) {
            int pos = location.lastIndexOf('.');
            if (pos != -1) {
                String ext = location.substring(pos, location.length());
                return forExtension(ext, includeInherited);
            }
        }
        return null;
    }

    /**
     * Prints all known ResourceType values to STDOUT, one per line, in the format "NAME: Description [.ext, ...]".
     * @param args not required
     */
    public static void main(String... args) {
        for (ResourceType type : values()) {
            System.out.printf("%s: %s %s%n", type.getName(), type.getDescription(), type.getExtensions());
        }
    }

}
