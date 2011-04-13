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
