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

package org.switchyard.validate.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.switchyard.common.cdi.CDIUtil;
import org.switchyard.common.type.Classes;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.metadata.JavaTypes;
import org.switchyard.validate.BaseValidator;
import org.switchyard.validate.ValidateMessages;
import org.switchyard.validate.ValidationResult;
import org.switchyard.validate.Validator;
import org.switchyard.validate.config.model.JavaValidateModel;

/**
 * Validator Utility methods.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public final class ValidatorUtil {

    private static final Logger LOGGER = Logger.getLogger(ValidatorUtil.class);

    private static final QName OBJECT_TYPE = JavaTypes.toMessageType(Object.class);

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
                    throw ValidateMessages.MESSAGES.cdiBeanManagerNotFound();
                }
                Object validator = CDIUtil.lookupBean(bean);
                if (validator == null) {
                    throw ValidateMessages.MESSAGES.validatorBeanNotFound(bean);
                }
                validators = newValidators(validator, validateModel.getName());

            } else {
                String className = ((JavaValidateModel) validateModel).getClazz();
                if (className == null) {
                    throw ValidateMessages.MESSAGES.beanOrClassRequired();
                }
                try {
                    Class<?> validateClass = Classes.forName(className, ValidatorUtil.class);
                    validators = newValidators(validateClass, validateModel.getName());
                } catch (Exception e) {
                    throw ValidateMessages.MESSAGES.errorConstructingValidator(className, e);
                }
            }
        } else {
            ValidatorFactory factory = newValidatorFactory(validateModel);

            validators = new ArrayList<Validator<?>>();
            validators.add(factory.newValidator(validateModel));
        }

        if (validators == null || validators.isEmpty()) {
            throw ValidateMessages.MESSAGES.unknownValidateModel(validateModel.getClass().getName());
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
            throw ValidateMessages.MESSAGES.invalidValidatorClass(clazz.getName());
        }

        final Object validatorObject;

        try {
            validatorObject = clazz.newInstance();
        } catch (Exception e) {
            throw ValidateMessages.MESSAGES.errorConstructingValidatorConstructorRequired(clazz.getName(), e);
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
            throw ValidateMessages.MESSAGES.errorConstructingValidatorClassNotSupported(validatorObject.getClass().getName(), 
                    name.toString());
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
            throw ValidateMessages.MESSAGES.errorConstructingValidatorMustHavePublicConstructor(clazz.getName(), e);
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
        if (!ValidationResult.class.isAssignableFrom(publicMethod.getReturnType())) {
            throw ValidateMessages.MESSAGES.invalidMethodSignatureMustReturnValidationResult(publicMethod.getName(), 
                    publicMethod.getDeclaringClass().getName());
        }
        
        Validator validator = new BaseValidator(name) {
            @Override
            public ValidationResult validate(Object subject) {
                try {
                    return ValidationResult.class.cast(publicMethod.invoke(validatorObject, subject));
                } catch (InvocationTargetException e) {
                    throw ValidateMessages.MESSAGES.errorExecutingValidatorInvocationTargetException(publicMethod.getName(),
                            publicMethod.getDeclaringClass().getName(), e);
                } catch (Exception e) {
                    throw ValidateMessages.MESSAGES.errorExecutingValidatorException(publicMethod.getName(),
                            publicMethod.getDeclaringClass().getName(), e);
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
            throw ValidateMessages.MESSAGES.invalidValidatorOneParameter(publicMethod.getName(),
                    publicMethod.getDeclaringClass().getName());
        }
        type = params[0];

        if (!validatorAnno.name().trim().equals("")) {
            name = QName.valueOf(validatorAnno.name().trim());
        } else {
            name = JavaTypes.toMessageType(type);
        }

        return new ValidatorMethod(name, publicMethod);
    }

    private static ValidatorFactory newValidatorFactory(ValidateModel validateModel) {
        ValidatorFactoryClass validatorFactoryClass = validateModel.getClass().getAnnotation(ValidatorFactoryClass.class);

        if (validatorFactoryClass == null) {
            throw ValidateMessages.MESSAGES.validateModelNotAnnotated(validateModel.getClass().getName());
        }

        Class<?> factoryClass = validatorFactoryClass.value();

        if (!org.switchyard.validate.internal.ValidatorFactory.class.isAssignableFrom(factoryClass)) {
            throw ValidateMessages.MESSAGES.invalidValidatorFactoryImplementation(org.switchyard.validate.internal.ValidatorFactory.class.getName());
        }

        try {
            return (org.switchyard.validate.internal.ValidatorFactory) factoryClass.newInstance();
        } catch (Exception e) {
            throw ValidateMessages.MESSAGES.failedToInstantiateValidatorFactory(factoryClass.getName());
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
