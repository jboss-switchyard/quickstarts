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

package org.switchyard.validate;

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
import org.switchyard.common.cdi.CDIUtil;
import org.switchyard.common.type.Classes;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.validate.config.model.JavaValidateModel;

/**
 * Validator Utility methods.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public final class ValidatorUtil {

    private static final Logger LOGGER = Logger.getLogger(ValidatorUtil.class);

    private static final QName OBJECT_TYPE = JavaService.toMessageType(Object.class);

    private ValidatorUtil() {}

    /**
     * Create a new {@link org.switchyard.validate.Validator} instance from the supplied {@link ValidateModel} instance.
     * @param validateModel The ValidateModel instance.
     * @return The Validator instance.
     */
    public static Validator<?> newValidator(ValidateModel validateModel) {
        return newValidators(validateModel).iterator().next();
    }

    /**
     * Create a Collection of {@link Validator} instances from the supplied {@link ValidateModel} instance.
     * @param validateModel The ValidateModel instance.
     * @return The Validator instance.
     */
    public static Collection<Validator<?>> newValidators(ValidateModel validateModel) {

        Collection<Validator<?>> validators = null;

        if (validateModel instanceof JavaValidateModel) {
            JavaValidateModel javaValidateModel = JavaValidateModel.class.cast(validateModel);
            String bean = javaValidateModel.getBean();
            if (bean != null) {
                if (CDIUtil.lookupBeanManager() == null) {
                    throw new SwitchYardException("CDI BeanManager couldn't be found. A Java validator class name must be specified if CDI is not enabled.");
                }
                Object validator = CDIUtil.lookupBean(bean);
                if (validator == null) {
                    throw new SwitchYardException("The Java validator bean '" + bean + "' couldn't be found in CDI registry.");
                }
                validators = newValidators(validator, validateModel.getName());

            } else {
                String className = ((JavaValidateModel) validateModel).getClazz();
                if (className == null) {
                    throw new SwitchYardException("'bean' or 'class' must be specified for Java validator definition.");
                }
                try {
                    Class<?> validateClass = Classes.forName(className, ValidatorUtil.class);
                    validators = newValidators(validateClass, validateModel.getName());
                } catch (Exception e) {
                    throw new SwitchYardException("Error constructing Validator instance for class '" + className + "'.", e);
                }
            }
        } else {
            ValidatorFactory factory = newValidatorFactory(validateModel);

            validators = new ArrayList<Validator<?>>();
            validators.add(factory.newValidator(validateModel));
        }

        if (validators == null || validators.isEmpty()) {
            throw new SwitchYardException("Unknown ValidateModel type '" + validateModel.getClass().getName() + "'.");
        }

        return validators;
    }

    /**
     * Create a new {@link org.switchyard.validate.Validator} instance from the supplied
     * Class and supporting the specified name.
     * @param clazz The Class representing the Validator.
     * @param name The name of type.
     * @return The collection of Validator instances.
     * @see #isValidator(Class)
     */
    public static Validator<?> newValidator(Class<?> clazz, QName name) {
        return newValidators(clazz, name).iterator().next();
    }

    /**
     * Create a Collection of {@link Validator} instances from the supplied
     * Class and supporting the specified name.
     * @param clazz The Class representing the Validator.
     * @param name The name of type.
     * @return The collection of Validator instances.
     * @see #isValidator(Class)
     */
    public static Collection<Validator<?>> newValidators(Class<?> clazz, QName name) {
        if (!isValidator(clazz)) {
            throw new SwitchYardException("Invalid Validator class '" + clazz.getName() + "'.  Must implement the Validator interface, or have methods annotated with the @Validator annotation.");
        }

        final Object validatorObject;

        try {
            validatorObject = clazz.newInstance();
        } catch (Exception e) {
            throw new SwitchYardException("Error constructing Validator instance for class '" + clazz.getName() + "'.  Class must have a public default constructor.", e);
        }

        return newValidators(validatorObject, name);
    }
    
    /**
     * Create a Collection of {@link Validator} instances from the supplied
     * object and supporting the specified name.
     * @param validatorObject The Validator instance
     * @param name The name of type.
     * @return The collection of Validator instances.
     * @see #isValidator(Class)
     */
    public static Collection<Validator<?>> newValidators(Object validatorObject, QName name) {
        boolean nameIsWild = isWildcardType(name);
        Collection<Validator<?>> validators = new ArrayList<Validator<?>>();

        Method[] publicMethods = validatorObject.getClass().getMethods();
        for (Method publicMethod : publicMethods) {
            org.switchyard.annotations.Validator validatorAnno = publicMethod.getAnnotation(org.switchyard.annotations.Validator.class);
            if (validatorAnno != null) {
                ValidatorMethod validatorMethod = toValidatorMethod(publicMethod, validatorAnno);

                if ((nameIsWild || validatorMethod.getName().equals(name)))  {
                    validators.add(newValidator(validatorObject, validatorMethod.getMethod(), validatorMethod.getName()));
                }
            }
        }

        if (validatorObject instanceof Validator) {
            Validator validator = (Validator) validatorObject;
            QName vldName = validator.getName();

            if (vldName.equals(OBJECT_TYPE)) {
                // Type info not specified on validator, so assuming it's a generic/multi-type validator...
                validators.add(validator);
            } else if ((nameIsWild || vldName.equals(name))) {
                // Matching (specific) or wildcard type info specified...
                validators.add(validator);
            } else if (isAssignableFrom(vldName, name)) {
                // Compatible Java types...
                validators.add(validator);
            }

            if (!nameIsWild) {
                validator.setName(name);
            }
        }

        if (validators.isEmpty()) {
            throw new SwitchYardException("Error constructing Validator instance for class '" + validatorObject.getClass().getName() + "'.  Class does not support a validation for type '" + name + "'.");
        }

        return validators;
    }

    /**
     * Create a list of all the possible validations that the supplied Class offers.
     * @param clazz The Class to be analyzed.
     * @return A Map containing the validation types, with the key/value representing the to/from.
     */
    public static List<ValidatorTypes> listValidations(Class<?> clazz) {
        Object validatorObject;
        List<ValidatorTypes> validations = new ArrayList<ValidatorTypes>();

        try {
            validatorObject = clazz.newInstance();
        } catch (Exception e) {
            throw new SwitchYardException("Error constructing Validator instance for class '" + clazz.getName() + "'.  Class must have a public default constructor.", e);
        }

        // If the class itself implements the Validator interface....
        if (validatorObject instanceof org.switchyard.validate.Validator) {
            QName name = ((Validator) validatorObject).getName();
            if (name != null) {
                ValidatorTypes validatorTypes = new ValidatorTypes(name);
                validations.add(validatorTypes);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("added: " + validatorTypes);
                }
            }
        }

        // If some of the class methods are annotated with the @Validator annotation...
        Method[] publicMethods = clazz.getMethods();
        for (Method publicMethod : publicMethods) {
            org.switchyard.annotations.Validator validatorAnno = publicMethod.getAnnotation(org.switchyard.annotations.Validator.class);
            if (validatorAnno != null) {
                ValidatorMethod validatorMethod = toValidatorMethod(publicMethod, validatorAnno);
                ValidatorTypes validatorTypes= new ValidatorTypes(validatorMethod.getName());
                validations.add(validatorTypes);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("added: " + validatorTypes);
                }
            }
        }

        Collections.sort(validations, new ValidatorTypesComparator());
        if (LOGGER.isDebugEnabled()) {
            for (ValidatorTypes validatorTypes : validations) {
                LOGGER.debug("sorted: " + validatorTypes);
            }
        }

        return validations;
    }

    /**
     * Is the supplied Class a SwitchYard Validator class.
     * <p/>
     * A SwitchYard Validator class is any class that either implements the {@link org.switchyard.validate.Validator}
     * interface, or has one or more methods annotated with the {@link org.switchyard.annotations.Validator @Validator} annotation.
     *
     * @param clazz The Class instance.
     * @return True if the class can be used as a SwitchYard Validator, otherwise false.
     */
    public static boolean isValidator(Class<?> clazz) {
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
        if (org.switchyard.validate.Validator.class.isAssignableFrom(clazz)) {
            return true;
        }

        Method[] publicMethods = clazz.getMethods();
        for (Method publicMethod : publicMethods) {
            if (publicMethod.isAnnotationPresent(org.switchyard.annotations.Validator.class)) {
                return true;
            }
        }

        return false;
    }

    private static Validator newValidator(final Object validatorObject, final Method publicMethod, QName name) {
        Validator validator = new BaseValidator(name) {
            @Override
            public boolean validate(Object subject) {
                try {
                    return Boolean.parseBoolean(publicMethod.invoke(validatorObject, subject).toString());
                } catch (InvocationTargetException e) {
                    throw new SwitchYardException("Error executing @Validator method '" + publicMethod.getName() + "' on class '" + publicMethod.getDeclaringClass().getName() + "'.", e.getCause());
                } catch (Exception e) {
                    throw new SwitchYardException("Error executing @Validator method '" + publicMethod.getName() + "' on class '" + publicMethod.getDeclaringClass().getName() + "'.", e);
                }
            }
            
            @Override
            public Class<?> getType() {
                return publicMethod.getParameterTypes()[0];
            }
        };

        return validator;
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

    private static ValidatorMethod toValidatorMethod(Method publicMethod, org.switchyard.annotations.Validator validatorAnno) {

        QName name;
        Class<?> type;

        Class<?>[] params = publicMethod.getParameterTypes();
        if (params.length != 1) {
            throw new SwitchYardException("Invalid @Validator method '" + publicMethod.getName() + "' on class '" + publicMethod.getDeclaringClass().getName() + "'.  Must have exactly 1 parameter.");
        }
        type = params[0];

        if (!validatorAnno.name().trim().equals("")) {
            name = QName.valueOf(validatorAnno.name().trim());
        } else {
            name = JavaService.toMessageType(type);
        }

        return new ValidatorMethod(name, publicMethod);
    }

    private static ValidatorFactory newValidatorFactory(ValidateModel validateModel) {
        ValidatorFactoryClass validatorFactoryClass = validateModel.getClass().getAnnotation(ValidatorFactoryClass.class);

        if (validatorFactoryClass == null) {
            throw new SwitchYardException("ValidateModel type '" + validateModel.getClass().getName() + "' is not annotated with an @ValidatorFactoryClass annotation.");
        }

        Class<?> factoryClass = validatorFactoryClass.value();

        if (!org.switchyard.validate.ValidatorFactory.class.isAssignableFrom(factoryClass)) {
            throw new SwitchYardException("Invalid ValidatorFactory implementation.  Must implement '" + org.switchyard.validate.ValidatorFactory.class.getName() + "'.");
        }

        try {
            return (org.switchyard.validate.ValidatorFactory) factoryClass.newInstance();
        } catch (Exception e) {
            throw new SwitchYardException("Failed to create an instance of ValidatorFactory '" + factoryClass.getName() + "'.  Class must have a public default constructor and not be abstract.");
        }
    }

    private static class ValidatorMethod extends ValidatorTypes {

        private Method _method;

        /**
         * Public constructor.
         *
         * @param name name of type.
         * @param publicMethod
         */
        ValidatorMethod(QName name, Method publicMethod) {
            super(name);
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
            return String.format("%s [name=%s, method=%s]", getClass().getSimpleName(), getName(), getMethod());
        }
    }

    private static final class ValidatorTypesComparator implements Comparator<ValidatorTypes> {
        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(ValidatorTypes vt1, ValidatorTypes vt2) {
            int c = String.valueOf(vt1.getName()).compareTo(String.valueOf(vt2.getName()));
            if (c == 0 && vt1 instanceof ValidatorMethod && vt2 instanceof ValidatorMethod) {
                ValidatorMethod vm1 = (ValidatorMethod)vt1;
                ValidatorMethod vm2 = (ValidatorMethod)vt2;
                c = String.valueOf(vm1.getMethod()).compareTo(String.valueOf(vm2.getMethod()));
            }
            return c;
        }
    }
}
