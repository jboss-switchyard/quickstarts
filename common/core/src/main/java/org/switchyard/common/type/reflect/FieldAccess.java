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
        if (fieldName == null) {
            field = null;
        } else {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException nsfe1) {
                try {
                    field = clazz.getField(fieldName);
                } catch (NoSuchFieldException nsfe2) {
                    field = null;
                }
            }
        }
        setField(field);
    }

    private void setField(Field field) {
        _field = field;
        if (!_field.isAccessible()) {
            _field.setAccessible(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _field.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getType() {
        return (Class<T>)_field.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadable() {
        return !Modifier.isStatic(_field.getModifiers());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable() {
        int mod = _field.getModifiers();
        return !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
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
        }
    }

    @Override
    public String toString() {
        Class<?> clazz = getType();
        String type = clazz != null ? clazz.getSimpleName() : null;
        return "FieldAccess(name=" + getName() + ", type=" + type + ", readable=" + isReadable() + ", writeable=" + isWriteable() + ")";
    }

}
