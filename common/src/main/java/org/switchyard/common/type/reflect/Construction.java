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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.switchyard.common.type.Classes;

/**
 * A utility class that makes it cleaner to construct objects using reflection.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Construction {

    private Construction() {}

    /**
     * Constructs a new object of the specified type name using it's no-arg constructor.
     * @param typeName the class type name of the object
     * @return the new object
     */
    public static Object construct(String typeName) {
        return construct(typeName, Construction.class);
    }

    /**
     * Constructs a new object of the specified type name using it's no-arg constructor.
     * @param typeName the class type name of the object
     * @param caller the class calling this method (for classloading purposes)
     * @return the new object
     */
    public static Object construct(String typeName, Class<?> caller) {
        return construct(typeName, (ClassLoader)(caller != null ? caller.getClassLoader() : null));
    }

    /**
     * Constructs a new object of the specified type name using it's no-arg constructor.
     * @param typeName the class type name of the object
     * @param loader the classloader to use for classloading
     * @return the new object
     */
    public static Object construct(String typeName, ClassLoader loader) {
        return construct(Classes.forName(typeName, loader));
    }

    /**
     * Constructs a new object of the specified type using it's no-arg constructor.
     * @param <T> the type of object
     * @param type the class type of the object
     * @return the new object
     */
    public static <T> T construct(Class<T> type) {
        return construct(type, new Class<?>[0], new Object[0]);
    }

    /**
     * Constructs a new object of the specified type name using a multi-arg constructor.
     * @param typeName the class type name of the object
     * @param paramTypeNames the type names of the params
     * @param params the param objects
     * @return the new object
     */
    public static Object construct(String typeName, String[] paramTypeNames, Object[] params) {
        return construct(typeName, paramTypeNames, params, Construction.class);
    }

    /**
     * Constructs a new object of the specified type name using a multi-arg constructor.
     * @param typeName the class type name of the object
     * @param paramTypeNames the type names of the params
     * @param params the param objects
     * @param caller the class calling this method (for classloading purposes)
     * @return the new object
     */
    public static Object construct(String typeName, String[] paramTypeNames, Object[] params, Class<?> caller) {
        return construct(typeName, paramTypeNames, params, (ClassLoader)(caller != null ? caller.getClassLoader() : null));
    }

    /**
     * Constructs a new object of the specified type name using a multi-arg constructor.
     * @param typeName the class type name of the object
     * @param paramTypeNames the type names of the params
     * @param params the param objects
     * @param loader the classloader to use for classloading
     * @return the new object
     */
    public static Object construct(String typeName, String[] paramTypeNames, Object[] params, ClassLoader loader) {
        Class<?> type = Classes.forName(typeName, loader);
        Class<?>[] paramTypes = new Class<?>[paramTypeNames.length];
        for (int i=0; i < paramTypeNames.length; i++) {
            paramTypes[i] = Classes.forName(paramTypeNames[i], loader);
        }
        return construct(type, paramTypes, params);
    }

    /**
     * Constructs a new object of the specified type name using a multi-arg constructor.
     * @param typeName the class type name of the object
     * @param paramTypes the types of the params
     * @param params the param objects
     * @return the new object
     */
    public static Object construct(String typeName, Class<?>[] paramTypes, Object[] params) {
        return construct(typeName, paramTypes, params, Construction.class);
    }

    /**
     * Constructs a new object of the specified type name using a multi-arg constructor.
     * @param typeName the class type name of the object
     * @param paramTypes the types of the params
     * @param params the param objects
     * @param caller the class calling this method (for classloading purposes)
     * @return the new object
     */
    public static Object construct(String typeName, Class<?>[] paramTypes, Object[] params, Class<?> caller) {
        return construct(typeName, paramTypes, params, caller != null ? caller.getClassLoader() : null);
    }

    /**
     * Constructs a new object of the specified type name using a multi-arg constructor.
     * @param typeName the class type name of the object
     * @param paramTypes the types of the params
     * @param params the param objects
     * @param loader the classloader to use for classloading
     * @return the new object
     */
    public static Object construct(String typeName, Class<?>[] paramTypes, Object[] params, ClassLoader loader) {
        Class<?> type = Classes.forName(typeName, loader);
        return construct(type, paramTypes, params);
    }

    /**
     * Constructs a new object of the specified type using a multi-arg constructor.
     * @param <T> the type of object
     * @param type the class type of the object
     * @param paramTypes the types of the params
     * @param params the param objects
     * @return the new object
     */
    public static <T> T construct(Class<T> type, Class<?>[] paramTypes, Object[] params) {
        Constructor<T> cnst = null;
        try {
            cnst = type.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException nsme1) {
            try {
                cnst = type.getConstructor(paramTypes);
            } catch (NoSuchMethodException nsme2) {
                throw new RuntimeException(nsme1);
            }
        }
        if (!cnst.isAccessible()) {
            cnst.setAccessible(true);
        }
        try {
            return cnst.newInstance(params);
        } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        } catch (InstantiationException ie) {
            throw new RuntimeException(ie);
        }
    }

}
