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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.switchyard.common.lang.Strings;

/**
 * Access via a wrapped Field.
 *
 * @param <T> the value type of this access
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class FieldAccess<T> implements Access<T> {

    private Field _field;

    /**
     * Constructs a new FieldAccess.
     * @param field the Field to wrap
     */
    public FieldAccess(Field field) {
        setField(field);
    }

    /**
     * Constructs a new FieldAccess.
     * @param clazz the declaring class
     * @param fieldName the field name
     */
    public FieldAccess(Class<?> clazz, String fieldName) {
        Field field;
        fieldName = Strings.trimToNull(fieldName);
        if (fieldName != null) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException nsfe1) {
                try {
                    field = clazz.getField(fieldName);
                } catch (NoSuchFieldException nsfe2) {
                    field = null;
                }
            }
            
            if (field != null) {
                setField(field);
            }
        }
    }

    private void setField(Field field) {
        _field = field;
        if ((field != null) && (!_field.isAccessible())) {
            _field.setAccessible(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        if (_field != null) {
            return _field.getName();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getType() {
        if (_field != null) {
            return (Class<T>)_field.getType();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadable() {
        if (_field != null) {
            return !Modifier.isStatic(_field.getModifiers());
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable() {
        if (_field != null) {
            int mod = _field.getModifiers();
            return !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T read(Object target) {
        try {
            return (T)_field.get(target);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        } catch (NullPointerException npe) {
            throw new RuntimeException(npe);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Object target, T value) {
        try {
            _field.set(target, value);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        } catch (NullPointerException npe) {
            throw new RuntimeException(npe);
        }
    }

    @Override
    public String toString() {
        Class<?> clazz = getType();
        String type = clazz != null ? clazz.getSimpleName() : null;
        return String.format("FieldAccess(name=%s, type=%s, readable=%b, writeable=%b)", 
                getName(), type, isReadable(), isWriteable());
    }

}
