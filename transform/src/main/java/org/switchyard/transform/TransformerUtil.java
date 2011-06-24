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

import org.switchyard.common.type.Classes;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.transform.config.model.JavaTransformModel;

import javax.xml.namespace.QName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Transformer Utility methods.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public final class TransformerUtil {

    private static final QName OBJECT_TYPE = JavaService.toMessageType(Object.class);

    private TransformerUtil() {}

    /**
     * Create a new {@link org.switchyard.transform.Transformer} instance from the supplied {@link TransformModel} instance.
     * @param transformModel The TransformModel instance.
     * @return The Transformer instance.
     */
    public static Transformer<?, ?> newTransformer(TransformModel transformModel) {
        return newTransformers(transformModel).iterator().next();
    }

    /**
     * Create a Collection of {@link Transformer} instances from the supplied {@link TransformModel} instance.
     * @param transformModel The TransformModel instance.
     * @return The Transformer instance.
     */
    public static Collection<Transformer<?, ?>> newTransformers(TransformModel transformModel) {

        Collection<Transformer<?, ?>> transformers = null;

        if (transformModel instanceof JavaTransformModel) {
            String className = ((JavaTransformModel) transformModel).getClazz();
            try {
                Class<?> transformClass = Classes.forName(className, TransformerUtil.class);

                transformers = newTransformers(transformClass, transformModel.getFrom(), transformModel.getTo());
            } catch (Exception e) {
                throw new SwitchYardException("Error constructing Transformer instance for class '" + className + "'.", e);
            }
        } else {
            TransformerFactory factory = newTransformerFactory(transformModel);

            transformers = new ArrayList<Transformer<?, ?>>();
            transformers.add(factory.newTransformer(transformModel));
        }

        if (transformers == null || transformers.isEmpty()) {
            throw new SwitchYardException("Unknown TransformModel type '" + transformModel.getClass().getName() + "'.");
        }

        return transformers;
    }

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

        boolean fromIsWild = isWildcardType(from);
        boolean toIsWild = isWildcardType(to);
        Collection<Transformer<?, ?>> transformers = new ArrayList<Transformer<?, ?>>();
        final Object transformerObject;

        try {
            transformerObject = clazz.newInstance();
        } catch (Exception e) {
            throw new SwitchYardException("Error constructing Transformer instance for class '" + clazz.getName() + "'.  Class must have a public default constructor.", e);
        }

        Method[] publicMethods = clazz.getMethods();
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
            throw new SwitchYardException("Error constructing Transformer instance for class '" + clazz.getName() + "'.  Class does not support a transformation from type '" + from + "' to type '" + to + "'.");
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
                transformations.add(new TransformerTypes(from, to));
            }
        }

        // If some of the class methods are annotated with the @Transformer annotation...
        Method[] publicMethods = clazz.getMethods();
        for (Method publicMethod : publicMethods) {
            org.switchyard.annotations.Transformer transformerAnno = publicMethod.getAnnotation(org.switchyard.annotations.Transformer.class);
            if (transformerAnno != null) {
                TransformerMethod transformerMethod = toTransformerMethod(publicMethod, transformerAnno);
                transformations.add(new TransformerTypes(transformerMethod.getFrom(), transformerMethod.getTo()));
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
        Transformer transformer = new BaseTransformer() {
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
        };

        transformer.setFrom(from);
        transformer.setTo(to);

        return transformer;
    }

    private static boolean isAssignableFrom(QName a, QName b) {
        if (JavaService.isJavaMessageType(a) && JavaService.isJavaMessageType(b)) {
            Class<?> aType = JavaService.toJavaMessageType(a);
            Class<?> bType = JavaService.toJavaMessageType(b);

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

    private static TransformerFactory newTransformerFactory(TransformModel transformModel) {
        TransformerFactoryClass transformerFactoryClass = transformModel.getClass().getAnnotation(TransformerFactoryClass.class);

        if (transformerFactoryClass == null) {
            throw new SwitchYardException("TransformModel type '" + transformModel.getClass().getName() + "' is not annotated with an @TransformerFactoryClass annotation.");
        }

        Class<?> factoryClass = transformerFactoryClass.value();

        if (!org.switchyard.transform.TransformerFactory.class.isAssignableFrom(factoryClass)) {
            throw new SwitchYardException("Invalid TransformerFactory implementation.  Must implement '" + org.switchyard.transform.TransformerFactory.class.getName() + "'.");
        }

        try {
            return (org.switchyard.transform.TransformerFactory) factoryClass.newInstance();
        } catch (Exception e) {
            throw new SwitchYardException("Failed to create an instance of TransformerFactory '" + factoryClass.getName() + "'.  Class must have a public default constructor and not be abstract.");
        }
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
    }
}
