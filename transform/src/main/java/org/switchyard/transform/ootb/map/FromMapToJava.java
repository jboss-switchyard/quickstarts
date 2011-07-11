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

package org.switchyard.transform.ootb.map;

import org.apache.log4j.Logger;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.Transformer;

import javax.xml.namespace.QName;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Transformer that takes a Map graph and from it, builds a Java object
 * graph.
 *
 * @param <F> From Type
 * @param <T> To Type.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class FromMapToJava<F, T> extends BaseTransformer<Map, Object> {

    private static Logger _logger = Logger.getLogger(FromMapToJava.class);

    private GraphBuilder _graphBuilder;

    private static Map<Class, Class> primitives = new HashMap<Class, Class>(); {
        primitives.put(Integer.TYPE, Integer.class);
        primitives.put(Long.TYPE, Long.class);
        primitives.put(Double.TYPE, Double.class);
        primitives.put(Float.TYPE, Float.class);
        primitives.put(Boolean.TYPE, Boolean.class);
        primitives.put(Character.TYPE, Character.class);
        primitives.put(Byte.TYPE, Byte.class);
        primitives.put(Short.TYPE, Short.class);
    }

    @Override
    public Transformer setTo(QName toType) {
        super.setTo(toType);

        if (!QNameUtil.isJavaMessageType(toType)) {
            throw new SwitchYardException("Invalid 'to' type '" + toType + "'.  Must be a Java Object type.");
        }

        Class<?> javaType = QNameUtil.toJavaMessageType(toType);
        if (javaType == null) {
            throw new SwitchYardException("Invalid 'to' type '" + toType + "'.  Class Not Found.");
        }

        _graphBuilder = new ComplexTypeBuilder(javaType, null, null);
        return this;
    }

    @Override
    public Object transform(Map from) {

        return _graphBuilder.build(from);
    }

    private abstract class GraphBuilder {

        private Class _javaType;
        private String _parentPropertyName;
        private GraphBuilder _parentNode;
        private Method _parentSetterMethod;
        private BeanInfo _beanInfo;

        private GraphBuilder(Class javaType, GraphBuilder parentBuilder, Method parentSetterMethod) {
            this._javaType = javaType;
            this._parentNode = parentBuilder;
            this._parentSetterMethod = parentSetterMethod;
            try {
                _beanInfo = Introspector.getBeanInfo(_javaType);
            } catch (IntrospectionException e) {
                throw new SwitchYardException("Failed to extract bean information from bean type '" + _javaType.getName() + "'.", e);
            }
        }

        public Class getJavaType() {
            return _javaType;
        }

        public String getParentPropertyName() {
            return _parentPropertyName;
        }

        public void setParentPropertyName(String parentPropertyName) {
            this._parentPropertyName = parentPropertyName;
        }

        abstract Object build(Object value);

        protected void setPropertyValue(Object instance, Object propertyVal, Method parentSetterMethod) throws IllegalAccessException, InvocationTargetException {
            Class<?> paramType = parentSetterMethod.getParameterTypes()[0];

            if (paramType.isInstance(propertyVal)) {
                parentSetterMethod.invoke(instance, propertyVal);
                return;
            }

            if (paramType.isPrimitive()) {
                paramType = primitives.get(paramType);
            }

            // Try building from a String ...
            try {
                Constructor<?> stringConstructor = paramType.getConstructor(String.class);
                parentSetterMethod.invoke(instance, stringConstructor.newInstance(propertyVal.toString()));
                return;
            } catch (Exception e) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Unable to set property '" + _parentPropertyName + "' on bean instance of type '" + instance.getClass().getName() + "'.", e);
                }
            }
        }

        protected GraphBuilder newBuilder(String propertyName, Class mapType) {
            Method propertySetter = getSetter(propertyName);
            Class propertyType = propertySetter.getParameterTypes()[0];
            GraphBuilder builder = newBuilder(mapType, propertyType, propertySetter);

            builder.setParentPropertyName(propertyName);

            return builder;
        }

        protected GraphBuilder newBuilder(Class mapType, Class javaType, Method parentSetter) {
            GraphBuilder nodeBuilder = null;

            if (Collection.class.isAssignableFrom(mapType)) {
                Type[] generics = parentSetter.getGenericParameterTypes();

                if (Collection.class.isAssignableFrom(javaType)) {
                    if (generics != null && generics.length == 1 && generics[0] != null) {
                        ParameterizedType genericType = (ParameterizedType) generics[0];
                        Type collectionEntryType = genericType.getActualTypeArguments()[0];

                        nodeBuilder = new CollectionTypeBuilder(javaType, (Class<?>) collectionEntryType, this, parentSetter);
                    }
                }
            } else {
                if (Map.class.isAssignableFrom(mapType)) {
                    nodeBuilder = new ComplexTypeBuilder(javaType, this, parentSetter);
                } else {
                    nodeBuilder = new SimpleTypeBuilder(javaType, this, parentSetter);
                }
            }

            return nodeBuilder;
        }

        private Method getSetter(String propertyName) {
            Method setterMethod = null;

            for (PropertyDescriptor propertyDesc : _beanInfo.getPropertyDescriptors()) {
                if (propertyDesc.getName().equals(propertyName)) {
                    setterMethod = propertyDesc.getWriteMethod();
                    break;
                }
            }

            if (setterMethod == null) {
                throw new SwitchYardException("No setter method for property '" + propertyName + "' on class '" + _javaType.getName() + "'.");
            }

            return setterMethod;
        }

        protected Object newInstance() {
            if (_javaType == Collection.class) {
                return new ArrayList();
            }
            if (_javaType == List.class) {
                return new ArrayList();
            }
            try {
                return _javaType.newInstance();
            } catch (Exception e) {
                throw new SwitchYardException("Unable to create instance of type '" + _javaType.getName() + "'.", e);
            }
        }
    }

    private final class SimpleTypeBuilder extends GraphBuilder {

        private SimpleTypeBuilder(Class javaType, GraphBuilder parentBuilder, Method parentSetterMethod) {
            super(javaType, parentBuilder, parentSetterMethod);
        }

        @Override
        Object build(Object value) {
            return value;
        }
    }

    private final class ComplexTypeBuilder extends GraphBuilder {

        private Map<String, GraphBuilder> _childNodes = new ConcurrentHashMap<String, GraphBuilder>();

        private ComplexTypeBuilder(Class javaType, GraphBuilder parentBuilder, Method parentSetterMethod) {
            super(javaType, parentBuilder, parentSetterMethod);
        }

        @Override
        Object build(Object value) {
            Object instance = newInstance();

            if (value instanceof Map && !((Map) value).isEmpty()) {
                Set properties = ((Map) value).entrySet();
                Iterator propertyIterator = properties.iterator();

                while (propertyIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) propertyIterator.next();
                    String propertyName = (String) entry.getKey();
                    Object propertyValue = entry.getValue();
                    GraphBuilder nodeBuilder = _childNodes.get(propertyName);

                    if (nodeBuilder == null) {
                        nodeBuilder = newBuilder(propertyName, propertyValue.getClass());
                        _childNodes.put(propertyName, nodeBuilder);
                    }

                    Object propertyVal = nodeBuilder.build(propertyValue);
                    try {
                        setPropertyValue(instance, propertyVal, nodeBuilder._parentSetterMethod);
                    } catch (Exception e) {
                        throw new SwitchYardException("Error invoking setter method '" + nodeBuilder._parentSetterMethod.getName() + "' on type '" + getJavaType().getName() + "'.", e);
                    }
                }
            }

            return instance;
        }
    }

    private final class CollectionTypeBuilder extends GraphBuilder {

        private volatile GraphBuilder _collectionEntryBuilder;
        private Class<?> _collectionEntryType;

        private CollectionTypeBuilder(Class<?> collectionType, Class<?> collectionEntryType, GraphBuilder parentBuilder, Method parentSetterMethod) {
            super(collectionType, parentBuilder, parentSetterMethod);
            this._collectionEntryType = collectionEntryType;
        }

        @Override
        Object build(Object value) {
            synchronized (this) {
                Collection instance = (Collection) newInstance();

                if (value instanceof Collection) {
                    for (Object arrayEntry : (Collection)value) {
                        if (_collectionEntryBuilder == null) {
                            _collectionEntryBuilder = newBuilder(arrayEntry.getClass(), _collectionEntryType, null);
                        }
                        instance.add(_collectionEntryBuilder.build(arrayEntry));
                    }
                }

                return instance;
            }
        }
    }
}
