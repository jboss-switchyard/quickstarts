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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.switchyard.common.lang.Strings;
import org.switchyard.common.CommonCoreMessages;

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
    public MethodAccess(Method readMethod, Method writeMethod) {
        setMethods(readMethod, writeMethod);
    }

    /**
     * Constructs a new MethodAccess.
     * @param clazz the declaring class
     * @param readMethodName the read method name
     * @param writeMethodName the write method name
     */
    public MethodAccess(Class<?> clazz, String readMethodName, String writeMethodName) {
        Method readMethod;
        readMethodName = Strings.trimToNull(readMethodName);
        if (readMethodName == null) {
            readMethod = null;
        } else {
            try {
                readMethod = clazz.getDeclaredMethod(readMethodName);
            } catch (NoSuchMethodException nsfe1) {
                try {
                    readMethod = clazz.getMethod(readMethodName);
                } catch (NoSuchMethodException nsfe2) {
                    readMethod = null;
                }
            }
        }
        Method writeMethod;
        writeMethodName = Strings.trimToNull(writeMethodName);
        if (writeMethodName == null) {
            writeMethod = null;
        } else {
            try {
                writeMethod = clazz.getDeclaredMethod(writeMethodName);
            } catch (NoSuchMethodException nsfe1) {
                try {
                    writeMethod = clazz.getMethod(writeMethodName);
                } catch (NoSuchMethodException nsfe2) {
                    writeMethod = null;
                }
            }
        }
        setMethods(readMethod, writeMethod);
    }

    @SuppressWarnings("unchecked")
    private void setMethods(Method readMethod, Method writeMethod) {
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
        return CommonCoreMessages.MESSAGES.methodAccessString(getName(), type, isReadable(), isWriteable());
    }

}
