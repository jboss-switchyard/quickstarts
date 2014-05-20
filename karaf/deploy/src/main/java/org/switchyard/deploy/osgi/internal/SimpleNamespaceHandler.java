/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.deploy.osgi.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Marshaller;
import org.switchyard.deploy.osgi.NamespaceHandler;

/**
 * SimpleNamespaceHandler.
 */
public class SimpleNamespaceHandler implements NamespaceHandler {

    private final Bundle _bundle;
    private final Properties _properties;

    public SimpleNamespaceHandler(Bundle bundle, Properties properties) {
        _bundle = bundle;
        _properties = properties;
    }

    @Override
    public URL getSchemaLocation(String namespace) {
        String prefix = getPrefix(namespace);
        if (prefix == null) {
            return null;
        }
        String location = _properties.getProperty(prefix + ".location");
        String schema = _properties.getProperty(prefix + ".schema");
        return _bundle.getResource(location + schema);
    }

    @Override
    public Marshaller createMarshaller(String namespace, Descriptor desc) {
        try {
            Class<Marshaller> clazz = getMarshallerClass(namespace);
            if (clazz != null) {
                return Construction.construct(clazz, new Class<?>[]{Descriptor.class}, new Object[]{desc});
            }
            return null;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create marshaller", e);
        }
    }

    @Override
    public Set<Class> getManagedClasses() {
        Set<Class> classes = new HashSet<Class>();
        for (String namespace : getNamespaces()) {
            Class<Marshaller> clazz = getMarshallerClass(namespace);
            if (clazz != null) {
                classes.add(clazz);
            }
        }
        return classes;
    }

    Class<Marshaller> getMarshallerClass(String namespace) {
        String prefix = getPrefix(namespace);
        if (prefix == null) {
            return null;
        }
        String className = _properties.getProperty(prefix + ".marshaller");
        if (className != null) {
            try {
                return (Class<Marshaller>) _bundle.loadClass(className);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to load marshaller", e);
            }
        }
        return null;
    }

    String getPrefix(String namespace) {
        for (String key : _properties.stringPropertyNames()) {
            if (key.endsWith(".namespace")) {
                if (namespace.equals(_properties.getProperty(key))) {
                    return key.substring(0, key.length() - ".namespace".length());
                }
            }
        }
        return null;
    }

    List<String> getNamespaces() {
        List<String> namespaces = new ArrayList<String>();
        for (String key : _properties.stringPropertyNames()) {
            if (key.endsWith(".namespace")) {
                namespaces.add(_properties.getProperty(key));
            }
        }
        return namespaces;
    }

}
