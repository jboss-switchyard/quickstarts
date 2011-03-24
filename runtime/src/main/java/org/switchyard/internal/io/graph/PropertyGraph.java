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
package org.switchyard.internal.io.graph;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.switchyard.internal.io.graph.Reflection.Access;
import org.switchyard.internal.io.graph.Reflection.BeanAccess;
import org.switchyard.internal.io.graph.Reflection.FieldAccess;
import org.switchyard.io.Serialization.AccessType;
import org.switchyard.io.Serialization.CoverageType;
import org.switchyard.io.Serialization.DefaultFactory;
import org.switchyard.io.Serialization.Exclude;
import org.switchyard.io.Serialization.Factory;
import org.switchyard.io.Serialization.Include;
import org.switchyard.io.Serialization.Strategy;

/**
 * A Graph representing a custom object, internalized as a Map of property names to other object Graphs.
 *
 * @param <T> the type of custom object
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class PropertyGraph<T> implements Graph<T> {

    private String _type;
    private Map<String,Graph<Object>> _properties;

    /**
     * Gets the type of custom object.
     * @return the type of custom object
     */
    public String getType() {
        return _type;
    }

    /**
     * Sets the type of custom object.
     * @param type the type of custom object
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * Gets the graphs representing the properties of the custom object.
     * @return the graphs representing the properties of the custom object
     */
    public Map<String,Graph<Object>> getProperties() {
        return _properties;
    }

    /**
     * Sets the graphs representing the properties of the custom object.
     * @param properties the graphs representing the properties of the custom object
     */
    public void setProperties(Map<String,Graph<Object>> properties) {
        _properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(T object) throws IOException {
        if (object != null) {
            Class<?> clazz = object.getClass();
            setType(clazz.getName());
            Map<String,Graph<Object>> props = null;
            for (Access access : getAccessList(clazz)) {
                if (!(access instanceof FieldAccess) && !access.isWriteable()) {
                    continue;
                }
                Object value = access.read(object);
                if (value != null) {
                    if (props == null) {
                        props = new LinkedHashMap<String,Graph<Object>>();
                    }
                    props.put(access.getName(), GraphBuilder.build(value));
                }
            }
            if (props != null) {
                setProperties(props);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public T decompose() throws IOException {
        String type = getType();
        if (type != null) {
            Class<T> clazz;
            try {
                clazz = (Class<T>)Class.forName(type);
            } catch (ClassNotFoundException cnfe) {
                throw new IOException(cnfe);
            }
            T object = getFactory(clazz).create(clazz);
            if (object != null) {
                for (Access access : getAccessList(clazz)) {
                    Map<String,Graph<Object>> props = getProperties();
                    if (props != null) {
                        Graph<Object> graph = props.get(access.getName());
                        if (graph != null) {
                            Object value = graph.decompose();
                            if (value != null) {
                                boolean skip = !access.isWriteable();
                                if (access instanceof FieldAccess) {
                                    Class<?> accessType = access.getType();
                                    if (GraphBuilder.isCollection(accessType) && value instanceof Collection) {
                                        ((Collection)access.read(object)).addAll((Collection)value);
                                        skip = true;
                                    } else if (GraphBuilder.isMap(accessType) && value instanceof Map) {
                                        ((Map)access.read(object)).putAll((Map)value);
                                        skip = true;
                                    }
                                }
                                if (!skip) {
                                    access.write(object, value);
                                }
                            }
                        }
                    }
                }
            }
            return object;
        }
        return null;
    }

    private List<Access> getAccessList(Class<?> clazz) {
        List<Access> accessList = new ArrayList<Access>();
        if (clazz.getAnnotation(Deprecated.class) != null) {
            return accessList;
        }
        Strategy strategy = clazz.getAnnotation(Strategy.class);
        AccessType accessType = strategy != null ? strategy.access() : AccessType.BEAN;
        CoverageType coverageType = strategy != null ? strategy.coverage() : CoverageType.INCLUSIVE;
        switch (accessType) {
            case BEAN:
                BeanInfo info;
                try {
                    info = Introspector.getBeanInfo(clazz);
                } catch (IntrospectionException ie) {
                    throw new RuntimeException(ie);
                }
                for (PropertyDescriptor desc : info.getPropertyDescriptors()) {
                    Method method = desc.getReadMethod();
                    if (((CoverageType.INCLUSIVE.equals(coverageType)
                            && method.getAnnotation(Exclude.class) == null)
                            || (CoverageType.EXCLUSIVE.equals(coverageType)
                                    && method.getAnnotation(Include.class) != null))
                                    && method.getAnnotation(Deprecated.class) == null) {
                        Access access = new BeanAccess(desc);
                        if (access.isReadable() && !"class".equals(access.getName())) {
                            accessList.add(access);
                        }
                    }
                }
                break;
            case FIELD:
                for (Field field : clazz.getDeclaredFields()) {
                    if (((CoverageType.INCLUSIVE.equals(coverageType)
                            && field.getAnnotation(Exclude.class) == null)
                            || (CoverageType.EXCLUSIVE.equals(coverageType)
                                    && field.getAnnotation(Include.class) != null))
                                    && field.getAnnotation(Deprecated.class) == null
                                    && !Modifier.isTransient(field.getModifiers())) {
                        Access access = new FieldAccess(field);
                        if (access.isReadable()) {
                            accessList.add(access);
                        }
                    }
                }
                break;
        }
        return accessList;
    }

    @SuppressWarnings("unchecked")
    private Factory<T> getFactory(Class<T> clazz) throws IOException {
        Strategy strategy = clazz.getAnnotation(Strategy.class);
        if (strategy != null) {
            try {
                return (Factory<T>)strategy.factory().newInstance();
            } catch (IllegalAccessException iae) {
                throw new IOException(iae);
            } catch (InstantiationException ie) {
                throw new IOException(ie);
            }
        }
        return new DefaultFactory<T>();
    }

}
