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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Access via wrapped read and write Methods.
 *
 * @param <T> the value type of this access
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class MethodAccess<T> implements Access<T> {

    private Method _readMethod;
    private Method _writeMethod;
    private String _name = null;
    private Class<T> _type;

    /**
     * Constructs a new MethodAccess.
     * @param readMethod the read Method to wrap
     * @param writeMethod the write Method to wrap
     */
    @SuppressWarnings("unchecked")
    public MethodAccess(Method readMethod, Method writeMethod) {
        if (readMethod != null) {
            _readMethod = readMethod;
            if (!_readMethod.isAccessible()) {
                _readMethod.setAccessible(true);
            }
            _type = (Class<T>)_readMethod.getReturnType();
            String name = _readMethod.getName();
            if (name.startsWith("get") || name.startsWith("is")) {
                name = name.startsWith("get") ? name.substring(3) : name.substring(2);
                char c = Character.toLowerCase(name.charAt(0));
                _name = new StringBuilder().append(c).append(name.substring(1)).toString();
            }
        }
        if (writeMethod != null) {
            _writeMethod = writeMethod;
            if (!_writeMethod.isAccessible()) {
                _writeMethod.setAccessible(true);
            }
            if (_type == null) {
                _type = (Class<T>)_writeMethod.getParameterTypes()[0];
            }
            if (_name == null) {
                String name = _writeMethod.getName();
                if (name.startsWith("set")) {
                    name = name.substring(3);
                    char c = Character.toLowerCase(name.charAt(0));
                    _name = new StringBuilder().append(c).append(name.substring(1)).toString();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getType() {
        return _type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadable() {
        return _readMethod != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable() {
        return _writeMethod != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T read(Object target) {
        if (_readMethod != null) {
            try {
                return (T)_readMethod.invoke(target);
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
        if (_writeMethod != null) {
            try {
                _writeMethod.invoke(target, value);
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
        return "MethodAccess(name=" + getName() + ", type=" + type + ", readable=" + isReadable() + ", writeable=" + isWriteable() + ")";
    }

}
