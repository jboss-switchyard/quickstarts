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
package org.switchyard.common.type.reflect;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Access via a wrapped PropertyDescriptor.
 *
 * @param <T> the value type of this access
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class BeanAccess<T> implements Access<T> {

    private PropertyDescriptor _propDesc;

    /**
     * Constructs a new BeanAccess.
     * @param propDesc the ProperyDescriptor to wrap
     */
    public BeanAccess(PropertyDescriptor propDesc) {
        _propDesc = propDesc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _propDesc.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getType() {
        return (Class<T>)_propDesc.getPropertyType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadable() {
        return _propDesc.getReadMethod() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable() {
        return _propDesc.getWriteMethod() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T read(Object target) {
        Method readMethod = _propDesc.getReadMethod();
        if (readMethod != null) {
            try {
                return (T)readMethod.invoke(target);
            } catch (IllegalAccessException iae) {
                throw new RuntimeException(iae);
            } catch (InvocationTargetException ite) {
                throw new RuntimeException(ite);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Object target, T value) {
        Method writeMethod = _propDesc.getWriteMethod();
        if (writeMethod != null) {
            try {
                writeMethod.invoke(target, value);
            } catch (IllegalAccessException iae) {
                throw new RuntimeException(iae);
            } catch (InvocationTargetException ite) {
                throw new RuntimeException(ite);
            }
        }
    }

    @Override
    public String toString() {
        Class<?> clazz = getType();
        String type = clazz != null ? clazz.getSimpleName() : null;
        return "BeanAccess(name=" + getName() + ", type=" + type + ", readable=" + isReadable() + ", writeable=" + isWriteable() + ")";
    }

}
