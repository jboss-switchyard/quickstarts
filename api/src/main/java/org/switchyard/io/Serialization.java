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
package org.switchyard.io;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;

/**
 * A collection of annotations, enums and an interface and default implementation for strategic serialization.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Serialization {

    private Serialization() {}

    /**
     * The serialization strategy.
     */
    @Target(TYPE)
    @Retention(RUNTIME)
    @Documented
    public static @interface Strategy {
        
        /**
         * The AccessType, default is BEAN.
         */
        AccessType access() default AccessType.BEAN;

        /**
         * The CoverageType, default is INCLUSIVE.
         */
        CoverageType coverage() default CoverageType.INCLUSIVE;

        /**
         * The Factory class, default is DefaultFactory.class.
         */
        @SuppressWarnings("rawtypes")
        Class<? extends Factory> factory() default DefaultFactory.class;

    }

    /**
     * Access types.
     */
    public static enum AccessType {
        /** de/serialize using javabean property methods. */
        BEAN,
        /** de/serialize using fields. */
        FIELD
    }

    /**
     * Coverage types.
     */
    public static enum CoverageType {
        /** Include all property methods or fields. */
        INCLUSIVE,
        /** Exclude all property methods or fields. */
        EXCLUSIVE
    }

    /**
     * Overrides an EXCLUSIVE coverage type. Place on a field or a "getter" method.
     */
    @Target({FIELD,METHOD})
    @Retention(RUNTIME)
    @Documented
    public static @interface Include {}

    /**
     * Overrides an INCLUSIVE coverage type. Place on a field or a "getter" method.
     */
    @Target({FIELD,METHOD})
    @Retention(RUNTIME)
    @Documented
    public static @interface Exclude {}

    /**
     * A factory for creating new objects of the specified type.
     */
    public static interface Factory<T> {

        /**
         * The factory method.
         * @param type the type of object to create
         * @return the new object
         * @throws IOException if a problem ocurrs during creation
         */
        public T create(Class<T> type) throws IOException;

    }

    /**
     * The default serialization factory. Assumes a no-arg constructor exists, and calls it.
     */
    public static final class DefaultFactory<T> implements Factory<T> {

        /**
         * {@inheritDoc}
         */
        @Override
        public T create(Class<T> type) throws IOException {
            try {
                Constructor<T> cnst = type.getConstructor();
                if (!cnst.isAccessible()) {
                    cnst.setAccessible(true);
                }
                return cnst.newInstance();
            } catch (Exception e) {
                if (e instanceof IOException) {
                    throw (IOException)e;
                } else {
                    throw new IOException(e);
                }
            }
        }

    }

}
