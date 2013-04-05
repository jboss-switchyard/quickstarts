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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.switchyard.common.type.Classes;
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
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class AccessNode implements Node {

    private String _type;
    private Map<String, Integer> _ids;
    private transient Set<Integer> _resolutions;

    /**
     * Default constructor.
     */
    public AccessNode() {}

    /**
     * Gets the type.
     * @return the type
     */
    public String getType() {
        return _type;
    }

    /**
     * Sets the type.
     * @param type the type
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * Gets the ids.
     * @return the ids
     */
    public Map<String, Integer> getIds() {
        return _ids;
    }

    /**
     * Sets the ids.
     * @param ids the ids
     */
    public void setIds(Map<String, Integer> ids) {
        _ids = ids;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Object obj, Graph graph) {
        if (obj != null) {
            Class<?> clazz = obj.getClass();
            setType(clazz.getName());
            for (Access<?> access : getAccessList(clazz)) {
                if (!(access instanceof FieldAccess) && !access.isWriteable()) {
                    continue;
                }
                Object value = access.read(obj);
                if (value != null) {
                    if (_ids == null) {
                        _ids = new LinkedHashMap<String, Integer>();
                    }
                    Integer id = NodeBuilder.build(value, graph);
                    if (id != null) {
                        _ids.put(access.getName(), id);
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
        if (_type == null) {
            return null;
        }
        final Class clazz = Classes.forName(_type, getClass());
        final Factory factory = Factory.getFactory(clazz);
        final Object obj = factory.supports(clazz) ? factory.create(clazz) : null;
        if (obj != null && _ids != null) {
            for (final Access access : getAccessList(clazz)) {
                final Integer id = _ids.get(access.getName());
                if (id != null) {
                    if (_resolutions == null) {
                        _resolutions = new HashSet<Integer>();
                    }
                    if (!_resolutions.contains(id)) {
                        _resolutions.add(id);
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
                            String name = readMethod.getName();
                            if (name.startsWith("get") || name.startsWith("is")) {
                                name = "set" + (name.startsWith("get") ? name.substring(3) : name.substring(2));
                                Class<?> readClass = readMethod.getDeclaringClass();
                                try {
                                    writeMethod = readClass.getDeclaredMethod(name, desc.getPropertyType());
                                } catch (NoSuchMethodException nsme1) {
                                    try {
                                        writeMethod = readClass.getMethod(name, desc.getPropertyType());
                                    } catch (NoSuchMethodException nsme2) {
                                        writeMethod = null;
                                    }
                                }
                                if (writeMethod != null) {
                                    Class<?> returnClass = writeMethod.getReturnType();
                                    if (returnClass == null || returnClass.isAssignableFrom(readClass)) {
                                        access = new MethodAccess(readMethod, writeMethod);
                                    }
                                }
                            }
                        }
                        if (access == null) {
                            access = new BeanAccess(desc);
                        }
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

}
