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
package org.switchyard.deploy.osgi.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Marshaller;
import org.switchyard.deploy.osgi.NamespaceHandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleNamespaceHandler implements NamespaceHandler {

    private final Bundle bundle;
    private final Properties properties;

    public SimpleNamespaceHandler(Bundle bundle, Properties properties) {
        this.bundle = bundle;
        this.properties = properties;
    }

    @Override
    public URL getSchemaLocation(String namespace) {
        String prefix = getPrefix(namespace);
        if (prefix == null) {
            return null;
        }
        String location = properties.getProperty(prefix + ".location");
        String schema = properties.getProperty(prefix + ".schema");
        return bundle.getResource(location + schema);
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
        String className = properties.getProperty(prefix + ".marshaller");
        if (className != null) {
            try {
                return (Class<Marshaller>) bundle.loadClass(className);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to load marshaller", e);
            }
        }
        return null;
    }

    String getPrefix(String namespace) {
        for (String key : properties.stringPropertyNames()) {
            if (key.endsWith(".namespace")) {
                if (namespace.equals(properties.getProperty(key))) {
                    return key.substring(0, key.length() - ".namespace".length());
                }
            }
        }
        return null;
    }

    List<String> getNamespaces() {
        List<String> namespaces = new ArrayList<String>();
        for (String key : properties.stringPropertyNames()) {
            if (key.endsWith(".namespace")) {
                namespaces.add(properties.getProperty(key));
            }
        }
        return namespaces;
    }

}
