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

package org.switchyard.transform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.java.JavaService;

/**
 * Transformer Utility methods.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public final class TransformerUtil {

    private static final Logger LOGGER = Logger.getLogger(TransformerUtil.class);

    private static final QName OBJECT_TYPE = JavaService.toMessageType(Object.class);

    private TransformerUtil() {}

    /**
     * Create a new {@link org.switchyard.transform.Transformer} instance from the supplied
     * Class and supporting the specified from and to.
     * @param clazz The Class representing the Transformer.
     * @param from The from type.
     * @param to The to type.
     * @return The collection of Transformer instances.
     * @see #isTransformer(Class)
     */
    public static Transformer<?, ?> newTransformer(Class<?> clazz, QName from, QName to) {
        return newTransformers(clazz, from, to).iterator().next();
    }

    /**
     * Create a Collection of {@link Transformer} instances from the supplied
     * Class and supporting the specified from and to.
     * @param clazz The Class representing the Transformer.
     * @param from The from type.
     * @param to The to type.
     * @return The collection of Transformer instances.
     * @see #isTransformer(Class)
     */
    public static Collection<Transformer<?, ?>> newTransformers(Class<?> clazz, QName from, QName to) {
        if (!isTransformer(clazz)) {
            throw new SwitchYardException("Invalid Transformer class '" + clazz.getName() + "'.  Must implement the Transformer interface, or have methods annotated with the @Transformer annotation.");
        }

        final Object transformerObject;
        try {
            transformerObject = clazz.newInstance();
        } catch (Exception e) {
            throw new SwitchYardException("Error constructing Transformer instance for class '" + clazz.getName() + "'.  Class must have a public default constructor.", e);
        }

        return TransformerUtil.newTransformers(transformerObject, from, to);
    }
    
    /**
     * Create a Collection of {@link Transformer} instances from the supplied
     * object and supporting the specified from and to.
     * @param transformerObject The Transformer instance
     * @param from The from type.
     * @param to The to type.
     * @return The collection of Transformer instances.
     * @see #isTransformer(Class)
     */
    public static Collection<Transformer<?, ?>> newTransformers(Object transformerObject, QName from, QName to) {
        boolean fromIsWild = isWildcardType(from);
        boolean toIsWild = isWildcardType(to);
        Collection<Transformer<?, ?>> transformers = new ArrayList<Transformer<?, ?>>();

        Method[] publicMethods = transformerObject.getClass().getMethods();
        for (Method publicMethod : publicMethods) {
            org.switchyard.annotations.Transformer transformerAnno = publicMethod.getAnnotation(org.switchyard.annotations.Transformer.class);
            if (transformerAnno != null) {
                TransformerMethod transformerMethod = toTransformerMethod(publicMethod, transformerAnno);

                if ((fromIsWild || transformerMethod.getFrom().equals(from)) && (toIsWild || transformerMethod.getTo().equals(to))) {
                    transformers.add(newTransformer(transformerObject, transformerMethod.getMethod(), transformerMethod.getFrom(), transformerMethod.getTo()));
                }
            }
        }

        if (transformerObject instanceof Transformer) {
            Transformer transformer = (Transformer) transformerObject;
            QName transFrom = transformer.getFrom();
            QName transTo = transformer.getTo();

            if (transFrom.equals(OBJECT_TYPE) && transTo.equals(OBJECT_TYPE)) {
                // Type info not specified on transformer, so assuming it's a generic/multi-type transformer...
                transformers.add(transformer);
            } else if ((fromIsWild || transFrom.equals(from)) && (toIsWild || transTo.equals(to))) {
                // Matching (specific) or wildcard type info specified...
                transformers.add(transformer);
            } else if (isAssignableFrom(transFrom, from) && isAssignableFrom(transTo, to)) {
                // Compatible Java types...
                transformers.add(transformer);
            }

            if (!fromIsWild) {
                transformer.setFrom(from);
            }
            if (!toIsWild) {
                transformer.setTo(to);
            }
        }

        if (transformers.isEmpty()) {
            throw new SwitchYardException("Error constructing Transformer instance for class '" + transformerObject.getClass().getName() + "'.  Class does not support a transformation from type '" + from + "' to type '" + to + "'.");
        }

        return transformers;
    }

    /**
     * Create a list of all the possible transformations that the supplied Class offers.
     * @param clazz The Class to be analyzed.
     * @return A Map containing the transformation types, with the key/value representing the to/from.
     */
    public static List<TransformerTypes> listTransformations(Class<?> clazz) {
        Object transformerObject;
        List<TransformerTypes> transformations = new ArrayList<TransformerTypes>();

        try {
            transformerObject = clazz.newInstance();
        } catch (Exception e) {
            throw new SwitchYardException("Error constructing Transformer instance for class '" + clazz.getName() + "'.  Class must have a public default constructor.", e);
        }

        // If the class itself implements the Transformer interface....
        if (transformerObject instanceof org.switchyard.transform.Transformer) {
            QName from = ((Transformer) transformerObject).getFrom();
            QName to = ((Transformer) transformerObject).getTo();
            if (from != null && to != null) {
                TransformerTypes transformerTypes = new TransformerTypes(from, to);
                transformations.add(transformerTypes);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("added: " + transformerTypes);
                }
            }
        }

        // If some of the class methods are annotated with the @Transformer annotation...
        Method[] publicMethods = clazz.getMethods();
        for (Method publicMethod : publicMethods) {
            org.switchyard.annotations.Transformer transformerAnno = publicMethod.getAnnotation(org.switchyard.annotations.Transformer.class);
            if (transformerAnno != null) {
                TransformerMethod transformerMethod = toTransformerMethod(publicMethod, transformerAnno);
                TransformerTypes transformerTypes = new TransformerTypes(transformerMethod.getFrom(), transformerMethod.getTo());
                transformations.add(transformerTypes);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("added: " + transformerTypes);
                }
            }
        }

        Collections.sort(transformations, new TransformerTypesComparator());
        if (LOGGER.isDebugEnabled()) {
            for (TransformerTypes transformerTypes : transformations) {
                LOGGER.debug("sorted: " + transformerTypes);
            }
        }

        return transformations;
    }

    /**
     * Is the supplied Class a SwitchYard Transformer class.
     * <p/>
     * A SwitchYard Transformer class is any class that either implements the {@link org.switchyard.transform.Transformer}
     * interface, or has one or more methods annotated with the {@link org.switchyard.annotations.Transformer @Transformer} annotation.
     *
     * @param clazz The Class instance.
     * @return True if the class can be used as a SwitchYard Transformer, otherwise false.
     */
    public static boolean isTransformer(Class<?> clazz) {
        if (clazz.isInterface()) {
            return false;
        }
        if (clazz.isAnnotation()) {
            return false;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }
        try {
            // Must have a default constructor...
            clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            return false;
        }
        if (org.switchyard.transform.Transformer.class.isAssignableFrom(clazz)) {
            return true;
        }

        Method[] publicMethods = clazz.getMethods();
        for (Method publicMethod : publicMethods) {
            if (publicMethod.isAnnotationPresent(org.switchyard.annotations.Transformer.class)) {
                return true;
            }
        }

        return false;
    }

    private static Transformer newTransformer(final Object transformerObject, final Method publicMethod, QName from, QName to) {
        Transformer transformer = new BaseTransformer(from, to) {
            @Override
            public Object transform(Object from) {
                try {
                    return publicMethod.invoke(transformerObject, from);
                } catch (InvocationTargetException e) {
                    throw new SwitchYardException("Error executing @Transformer method '" + publicMethod.getName() + "' on class '" + publicMethod.getDeclaringClass().getName() + "'.", e.getCause());
                } catch (Exception e) {
                    throw new SwitchYardException("Error executing @Transformer method '" + publicMethod.getName() + "' on class '" + publicMethod.getDeclaringClass().getName() + "'.", e);
                }
            }
            
            @Override
            public Class<?> getFromType() {
                return publicMethod.getParameterTypes()[0];
            }
            
            @Override
            public Class<?> getToType() {
                return publicMethod.getReturnType();
            }
        };

        return transformer;
    }

    private static boolean isAssignableFrom(QName a, QName b) {
        if (QNameUtil.isJavaMessageType(a) && QNameUtil.isJavaMessageType(b)) {
            Class<?> aType = QNameUtil.toJavaMessageType(a);
            Class<?> bType = QNameUtil.toJavaMessageType(b);

            if (aType == null || bType == null) {
                return false;
            }

            return aType.isAssignableFrom(bType);
        }

        return false;
    }

    private static boolean isWildcardType(QName type) {
        return type.toString().equals("*");
    }

    private static TransformerMethod toTransformerMethod(Method publicMethod, org.switchyard.annotations.Transformer transformerAnno) {

        QName from;
        QName to;
        Class<?> fromType;
        Class<?> toType;

        Class<?>[] params = publicMethod.getParameterTypes();
        if (params.length != 1) {
            throw new SwitchYardException("Invalid @Transformer method '" + publicMethod.getName() + "' on class '" + publicMethod.getDeclaringClass().getName() + "'.  Must have exactly 1 parameter.");
        }
        fromType = params[0];
        toType = publicMethod.getReturnType();
        if (toType == null) {
            throw new SwitchYardException("Invalid @Transformer method '" + publicMethod.getName() + "' on class '" + publicMethod.getDeclaringClass().getName() + "'.  Must return a result.");
        }

        if (!transformerAnno.from().trim().equals("")) {
            from = QName.valueOf(transformerAnno.from().trim());
        } else {
            from = JavaService.toMessageType(fromType);
        }
        if (!transformerAnno.to().trim().equals("")) {
            to = QName.valueOf(transformerAnno.to().trim());
        } else {
            to = JavaService.toMessageType(toType);
        }

        return new TransformerMethod(from, to, publicMethod);
    }

    private static class TransformerMethod extends TransformerTypes {

        private Method _method;

        /**
         * Public constructor.
         *
         * @param from From type.
         * @param to   To type.
         * @param publicMethod
         */
        TransformerMethod(QName from, QName to, Method publicMethod) {
            super(from, to);
            this._method = publicMethod;
        }

        private Method getMethod() {
            return _method;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return String.format("%s [from=%s, to=%s, method=%s]", getClass().getSimpleName(), getFrom(), getTo(), getMethod());
        }
    }

    private static final class TransformerTypesComparator implements Comparator<TransformerTypes> {
        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(TransformerTypes tt1, TransformerTypes tt2) {
            int c = String.valueOf(tt1.getFrom()).compareTo(String.valueOf(tt2.getFrom()));
            if (c == 0) {
                c = String.valueOf(tt1.getTo()).compareTo(String.valueOf(tt2.getTo()));
                if (c == 0 && tt1 instanceof TransformerMethod && tt2 instanceof TransformerMethod) {
                    TransformerMethod tm1 = (TransformerMethod)tt1;
                    TransformerMethod tm2 = (TransformerMethod)tt2;
                    c = String.valueOf(tm1.getMethod()).compareTo(String.valueOf(tm2.getMethod()));
                }
            }
            return c;
        }
    }
}
