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
package org.switchyard.serial.graph.node;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.switchyard.common.type.reflect.Access;
import org.switchyard.common.type.reflect.BeanAccess;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.common.type.reflect.MethodAccess;
import org.switchyard.serial.graph.AccessType;
import org.switchyard.serial.graph.CoverageType;
import org.switchyard.serial.graph.Exclude;
import org.switchyard.serial.graph.Factory;
import org.switchyard.serial.graph.Graph;
import org.switchyard.serial.graph.Include;
import org.switchyard.serial.graph.Strategy;

/**
 * Reflection-based node for arbitrary objects.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
@SuppressWarnings("serial")
public abstract class AccessNode implements Node {

    static final Set<String> IGNORED_ACCESS_NAMES;
    static {
        Set<String> ignoredAccessNames = new HashSet<String>();
        ignoredAccessNames.add("class");
        ignoredAccessNames.add("ignoredAccessNames");
        IGNORED_ACCESS_NAMES = Collections.unmodifiableSet(ignoredAccessNames);
    }

    /**
     * Gets the set of ignored access names.
     * @return the set of ignored access names.
     */
    Set<String> getIgnoredAccessNames() {
        return IGNORED_ACCESS_NAMES;
    }

    /**
     * Gets the class.
     * @return the class.
     */
    public abstract Integer getClazz();

    /**
     * Sets the class.
     * @param clazz the class
     */
    public abstract void setClazz(Integer clazz);

    /**
     * Gets the ids.
     * @return the ids
     */
    public abstract Map<String, Integer> getIds();

    /**
     * Sets the ids.
     * @param ids the ids
     */
    public abstract void setIds(Map<String, Integer> ids);

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Object obj, Graph graph) {
        if (obj != null) {
            Class<?> clazz = obj.getClass();
            setClazz(NodeBuilder.build(clazz, graph));
            for (Access<?> access : getAccessList(clazz)) {
                if (!(access instanceof FieldAccess) && !access.isWriteable()) {
                    continue;
                }
                Object value = access.read(obj);
                if (value != null) {
                    Map<String, Integer> ids = getIds();
                    if (ids == null) {
                        ids = new LinkedHashMap<String, Integer>();
                        setIds(ids);
                    }
                    Integer id = NodeBuilder.build(value, graph);
                    if (id != null) {
                        ids.put(access.getName(), id);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object decompose(final Graph graph) {
        if (getClazz() == null) {
            return null;
        }
        final Class clazz = (Class)graph.decomposeReference(getClazz());
        final Factory factory = Factory.getFactory(clazz);
        final Object obj = factory.supports(clazz) ? factory.create(clazz, this) : null;
        Map<String, Integer> ids = getIds();
        if (obj != null && ids != null) {
            for (final Access access : getAccessList(clazz)) {
                final Integer id = ids.get(access.getName());
                if (id != null) {
                    graph.addResolution(new Runnable() {
                        public void run() {
                            Object value = graph.decomposeReference(id);
                            if (value != null) {
                                boolean skip = !access.isWriteable();
                                Class<?> accessType = access.getType();
                                if (access instanceof FieldAccess) {
                                    if (NodeBuilder.isCollection(accessType) && value instanceof Collection) {
                                        ((Collection)access.read(obj)).addAll((Collection)value);
                                        skip = true;
                                    } else if (NodeBuilder.isMap(accessType) && value instanceof Map) {
                                        ((Map)access.read(obj)).putAll((Map)value);
                                        skip = true;
                                    }
                                }
                                if (!skip) {
                                    if (NodeBuilder.isArray(accessType) && value.getClass().isArray()) {
                                        Object[] old_array = (Object[])value;
                                        Object[] new_array = (Object[])Array.newInstance(accessType.getComponentType(), old_array.length);
                                        System.arraycopy(old_array, 0, new_array, 0, old_array.length);
                                        value = new_array;
                                    }
                                    access.write(obj, value);
                                }
                            }
                        }
                    });
                }
            }
        }
        return obj;
    }

    @SuppressWarnings("rawtypes")
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
                    Method readMethod = desc.getReadMethod();
                    if (((CoverageType.INCLUSIVE.equals(coverageType)
                            && readMethod.getAnnotation(Exclude.class) == null)
                            || (CoverageType.EXCLUSIVE.equals(coverageType)
                                    && readMethod.getAnnotation(Include.class) != null))
                                    && readMethod.getAnnotation(Deprecated.class) == null) {
                        Access access = null;
                        Method writeMethod = desc.getWriteMethod();
                        if (writeMethod == null) {
                            String readName = readMethod.getName();
                            if (readName.startsWith("get") || readName.startsWith("is")) {
                                String writeName = "set" + (readName.startsWith("get") ? readName.substring(3) : readName.substring(2));
                                Class<?> declaringClass = readMethod.getDeclaringClass();
                                try {
                                    writeMethod = declaringClass.getDeclaredMethod(writeName, desc.getPropertyType());
                                } catch (NoSuchMethodException nsme1) {
                                    try {
                                        writeMethod = declaringClass.getMethod(writeName, desc.getPropertyType());
                                    } catch (NoSuchMethodException nsme2) {
                                        writeMethod = null;
                                    }
                                }
                                if (writeMethod != null) {
                                    Class<?> returnClass = writeMethod.getReturnType();
                                    if (returnClass == null || returnClass.isAssignableFrom(declaringClass)) {
                                        access = new MethodAccess(readMethod, writeMethod);
                                    }
                                }
                            }
                        }
                        if (access == null) {
                            access = new BeanAccess(desc);
                        }
                        if (access.isReadable() && !getIgnoredAccessNames().contains(access.getName())) {
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

}
