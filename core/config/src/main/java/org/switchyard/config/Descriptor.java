/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.config;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Descriptor.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Descriptor {

    public static final String DEFAULT_PROPERTIES = "/org/switchyard/config/descriptor.properties";
    public static final String NAMESPACE = "namespace";
    public static final String SCHEMA_LOCATION = "schemaLocation";

    private Map<String,String> _all_properties_map = new TreeMap<String,String>();
    private Map<String,Map<String,String>> _prefix_config_map = new HashMap<String,Map<String,String>>();
    private Map<String,String> _namespace_prefix_map = new HashMap<String,String>();

    public Descriptor() {
        try {
            setProperties(new PropertiesResource().pull(DEFAULT_PROPERTIES));
        } catch (IOException ioe) {
            // should never happen
            throw new RuntimeException(ioe);
        }
    }

    public Descriptor(Properties props) {
        setProperties(props);
    }

    private void setProperties(Properties props) {
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            String prop_name = (String)e.nextElement();
            String prop_value = props.getProperty(prop_name);
            if (prop_value != null) {
                _all_properties_map.put(prop_name, prop_value);
                StringTokenizer tokenizer = new StringTokenizer(prop_name, ".");
                String prop_prefix = tokenizer.nextToken().trim();
                String prop_suffix = tokenizer.nextToken().trim();
                Map<String,String> config = _prefix_config_map.get(prop_prefix);
                if (config == null) {
                    config = new HashMap<String,String>();
                    _prefix_config_map.put(prop_prefix, config);
                }
                config.put(prop_suffix, prop_value);
                if (NAMESPACE.equals(prop_suffix)) {
                    _namespace_prefix_map.put(prop_value, prop_prefix);
                }
            }
        }
    }

    public String getProperty(String property, String namespace) {
        String prop_prefix = _namespace_prefix_map.get(namespace);
        if (prop_prefix != null) {
            Map<String,String> config = _prefix_config_map.get(prop_prefix);
            if (config != null) {
                return config.get(property);
            }
        }
        return null;
    }

    public String getSchemaLocation(String namespace) {
        return getProperty(SCHEMA_LOCATION, namespace);
    }

    public String toString() {
        return _all_properties_map.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime  * result + ((_all_properties_map == null) ? 0 : _all_properties_map.hashCode());
        return result;
    }

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
        Descriptor other = (Descriptor)obj;
        if (_all_properties_map == null) {
            if (other._all_properties_map != null) {
                return false;
            }
        } else if (!_all_properties_map.equals(other._all_properties_map)) {
            return false;
        }
        return true;
    }

}
