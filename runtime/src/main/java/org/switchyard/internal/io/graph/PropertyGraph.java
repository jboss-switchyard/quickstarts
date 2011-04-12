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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.switchyard.internal.io.Reflection.Access;
import org.switchyard.internal.io.Reflection.BeanAccess;
import org.switchyard.internal.io.Reflection.FieldAccess;
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
    private LinkedHashMap<String,Graph<?>> _properties;

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
    public LinkedHashMap<String,Graph<?>> getProperties() {
        return _properties;
    }

    /**
     * Sets the graphs representing the properties of the custom object.
     * @param properties the graphs representing the properties of the custom object
     */
    public void setProperties(LinkedHashMap<String,Graph<?>> properties) {
        _properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void compose(T object, Map<Integer,Object> visited) throws IOException {
        if (object != null) {
            Integer id = Integer.valueOf(System.identityHashCode(object));
            if (!visited.containsKey(id)) {
                visited.put(id, (Graph<Object>)this);
                Class<T> clazz = (Class<T>)object.getClass();
                setType(clazz.getName());
                LinkedHashMap<String,Graph<?>> props = null;
                for (Access<?> access : getAccessList(clazz)) {
                    if (!(access instanceof FieldAccess) && !access.isWriteable()) {
                        continue;
                    }
                    Object value = access.read(object);
                    if (value != null) {
                        if (props == null) {
                            props = new LinkedHashMap<String,Graph<?>>();
                        }
                        props.put(access.getName(), GraphBuilder.build(value, visited));
                    }
                }
                if (props != null) {
                    setProperties(props);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public T decompose(Map<Integer,Object> visited) throws IOException {
        if (visited == null) {
            visited = new LinkedHashMap<Integer,Object>();
        }
        Integer id = Integer.valueOf(System.identityHashCode(this));
        T object = (T)visited.get(id);
        String type = getType();
        if (object == null && type != null) {
            Class<T> clazz;
            try {
                clazz = (Class<T>)Class.forName(type);
            } catch (ClassNotFoundException cnfe) {
                throw new IOException(cnfe);
            }
            object = getFactory(clazz).create(clazz);
            if (object != null) {
                visited.put(id, object);
                for (Access access : getAccessList(clazz)) {
                    Map<String,Graph<?>> props = getProperties();
                    if (props != null) {
                        Graph<?> graph = props.get(access.getName());
                        if (graph != null) {
                            Object value = graph.decompose(visited);
                            if (value != null) {
                                boolean skip = !access.isWriteable();
                                Class<?> accessType = access.getType();
                                Class<?> valueType = value.getClass();
                                if (access instanceof FieldAccess) {
                                    if (GraphBuilder.isCollection(accessType) && value instanceof Collection) {
                                        ((Collection)access.read(object)).addAll((Collection)value);
                                        skip = true;
                                    } else if (GraphBuilder.isMap(accessType) && value instanceof Map) {
                                        ((Map)access.read(object)).putAll((Map)value);
                                        skip = true;
                                    }
                                }
                                if (!skip) {
                                    if (GraphBuilder.isArray(accessType) && valueType.isArray()) {
                                        Object[] old_array = (Object[])value;
                                        Object[] new_array = (Object[])Array.newInstance(accessType.getComponentType(), old_array.length);
                                        System.arraycopy(old_array, 0, new_array, 0, old_array.length);
                                        value = new_array;
                                    }
                                    access.write(object, value);
                                }
                            }
                        }
                    }
                }
            }
        }
        return object;
    }

    private List<Access<?>> getAccessList(Class<?> clazz) {
        List<Access<?>> accessList = new ArrayList<Access<?>>();
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
                        @SuppressWarnings("rawtypes")
                        Access<?> access = new BeanAccess(desc);
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
                        @SuppressWarnings("rawtypes")
                        Access<?> access = new FieldAccess(field);
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

    @Override
    public String toString() {
        String type = getType();
        if (type != null) {
            int pos = type.lastIndexOf('$');
            if (pos == -1) {
                pos = type.lastIndexOf('.');
            }
            if (pos != -1) {
                type = type.substring(pos+1, type.length());
            }
        }
        StringBuilder props = new StringBuilder();
        LinkedHashMap<String,Graph<?>> properties = getProperties();
        if (properties != null) {
            props.append('[');
            boolean started = false;
            for (Map.Entry<String,Graph<?>> entry : properties.entrySet()) {
                if (!started) {
                    started = true;
                } else {
                    props.append(',');
                }
                props.append(entry.getKey());
            }
            props.append(']');
        } else {
            props.append("null");
        }
        return "PropertyGraph(type=" + type + ", properties=" + props + ")";
    }

}
