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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A utility class that makes it easier to manipulate fields and methods using reflection.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Reflection {

    private Reflection() {}

    /**
     * An abstraction of field and method access.
     */
    public static interface Access {

        /**
         * The name of the wrapped access mechanism.
         * @return the name
         */
        public String getName();

        /**
         * The Class type of the wrapped access mechanism.
         * @return the Class type
         */
        public Class<?> getType();

        /**
         * Whether the wrapped access mechanism is readable.
         * @return true if it's readable
         */
        public boolean isReadable();

        /**
         * Whether the wrapped access mechanism is writable.
         * @return true if it's writable
         */
        public boolean isWriteable();

        /**
         * Reads via the wrapped access mechanism targeting the specified object.
         * @param target the target object to read from
         * @return the read value
         */
        public Object read(Object target);

        /**
         * Writes via the wrapped access mechanism targeting the specified object.
         * @param target the target object to write to
         * @param value to value to write
         */
        public void write(Object target, Object value);

    }

    /**
     * Access via a wrapped Field.
     */
    public static final class FieldAccess implements Access {

        private Field _field;

        /**
         * Consructs a new FieldAccess.
         * @param field the Field to wrap
         */
        public FieldAccess(Field field) {
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
        public Class<?> getType() {
            return _field.getType();
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
        public Object read(Object target) {
            try {
                return _field.get(target);
            } catch (IllegalAccessException iae) {
                throw new RuntimeException(iae);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void write(Object target, Object value) {
            try {
                _field.set(target, value);
            } catch (IllegalAccessException iae) {
                throw new RuntimeException(iae);
            }
        }

    }

    /**
     * Access via a wrapped PropertyDescriptor.
     */
    public static final class BeanAccess implements Access {

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
        public Class<?> getType() {
            return _propDesc.getPropertyType();
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
        public Object read(Object target) {
            Method readMethod = _propDesc.getReadMethod();
            if (readMethod != null) {
                try {
                    return readMethod.invoke(target);
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
        public void write(Object target, Object value) {
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

    }

    /**
     * Access via wrapped read and write Methods.
     */
    public static final class MethodAccess implements Access {

        private Method _readMethod;
        private Method _writeMethod;
        private String _name = null;
        private Class<?> _type;

        /**
         * Constructs a new MethodAccess.
         * @param readMethod the read Method to wrap
         * @param writeMethod the write Method to wrap
         */
        public MethodAccess(Method readMethod, Method writeMethod) {
            if (readMethod != null) {
                _readMethod = readMethod;
                if (!_readMethod.isAccessible()) {
                    _readMethod.setAccessible(true);
                }
                _type = _readMethod.getReturnType();
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
                    _type = _writeMethod.getParameterTypes()[0];
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
        public Class<?> getType() {
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
        public Object read(Object target) {
            if (_readMethod != null) {
                try {
                    return _readMethod.invoke(target);
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
        public void write(Object target, Object value) {
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
    }

}
