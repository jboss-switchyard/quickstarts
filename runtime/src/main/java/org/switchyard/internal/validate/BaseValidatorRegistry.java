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

package org.switchyard.internal.validate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.event.EventPublisher;
import org.switchyard.event.ValidatorAddedEvent;
import org.switchyard.event.ValidatorRemovedEvent;
import org.switchyard.runtime.RuntimeMessages;
import org.switchyard.validate.Validator;
import org.switchyard.validate.ValidatorRegistry;

/**
 * Maintains a local collection of validation instances and provides
 * facilities to add, query, and remove validates.
 */
public class BaseValidatorRegistry implements ValidatorRegistry {

    private static Logger _log = Logger.getLogger(BaseValidatorRegistry.class);

    private final ConcurrentHashMap<QName, Validator<?>> _validators =
        new ConcurrentHashMap<QName, Validator<?>>();
    private final ConcurrentHashMap<QName, Validator<?>> _fallbackValidators =
            new ConcurrentHashMap<QName, Validator<?>>();

    private EventPublisher _eventPublisher;
    
    /**
     * Constructor.
     */
    public BaseValidatorRegistry() {
    }

    /**
     * Create a new ValidatorRegistry instance and add the list of validators
     * to the registry.
     * @param validators set of validators to add to registry
     */
    public BaseValidatorRegistry(Set<Validator<?>> validators) {
        for (Validator<?> t : validators) {
            addValidator(t);
        }
    }

    @Override
    public BaseValidatorRegistry addValidator(Validator<?> validator) {
        _fallbackValidators.clear();
        _validators.put(validator.getName(), validator);
        publishEvent(new ValidatorAddedEvent(validator));
        return this;
    }

    @Override
    public ValidatorRegistry addValidator(Validator<?> validator, QName name) {
        _fallbackValidators.clear();
        _validators.put(name, validator);
        publishEvent(new ValidatorAddedEvent(validator));
        return null;
    }

    @Override
    public Validator<?> getValidator(QName name) {
        Validator<?> validator = _validators.get(name);

        if (validator == null) {
            validator = _fallbackValidators.get(name);
            if (validator == null && QNameUtil.isJavaMessageType(name)) {
                validator = getJavaFallbackValidator(name);
                if (validator != null && _log.isDebugEnabled()) {
                    _log.debug("Selecting fallback validator: name '" + validator.getName() + "'. Type: " + validator.getClass().getName());
                } else if (_log.isDebugEnabled()) {
                    _log.debug("No compatible validator registered: name '" + name + "'");
                }
            }
        } else if (_log.isDebugEnabled()) {
            _log.debug("Selecting validator: name '" + validator.getName() + "'. Type: " + validator.getClass().getName());
        }
        
        return validator;
    }

    @Override
    public boolean hasValidator(QName name) {
        return _validators.containsKey(name);
    }

    @Override
    public boolean removeValidator(Validator<?> validator) {
        _fallbackValidators.clear();
        boolean removed = _validators.remove(validator.getName()) != null;
        if (removed) {
            publishEvent(new ValidatorRemovedEvent(validator));
        }
        
        return removed;
    }
    
    /**
     * Sets the EventPublisher instance to be used for validator events.
     * @param eventPublisher event publisher
     */
    public void setEventPublisher(EventPublisher eventPublisher) {
        _eventPublisher = eventPublisher;
    }

    // Convenience method to guard against cases when an event publisher has 
    // not been set.
    private void publishEvent(EventObject event) {
        if (_eventPublisher != null) {
            _eventPublisher.publish(event);
        }
    }

    private Validator<?> getJavaFallbackValidator(QName name) {
        Class<?> javaType = QNameUtil.toJavaMessageType(name);

        if (javaType == null) {
            return null;
        }

        Set<Map.Entry<QName,Validator<?>>> entrySet = _validators.entrySet();
        List<JavaSourceFallbackValidator> fallbackValidates = new ArrayList<JavaSourceFallbackValidator>();
        for (Map.Entry<QName,Validator<?>> entry : entrySet) {
            QName nameKey = entry.getKey();

            if (QNameUtil.isJavaMessageType(nameKey)) {
                Class<?> candidateType = QNameUtil.toJavaMessageType(nameKey);
                if (candidateType != null && candidateType.isAssignableFrom(javaType)) {
                    fallbackValidates.add(new JavaSourceFallbackValidator(candidateType, entry.getValue()));
                }
            }
        }

        if (fallbackValidates.size() == 0) {
            // No fallback...
            return null;
        }
        if (fallbackValidates.size() == 1) {
            Validator<?> fallbackValidator = fallbackValidates.get(0).getValidator();
            addFallbackValidator(fallbackValidator, name);
            return fallbackValidator;
        }

        JavaSourceFallbackValidatorComparator comparator = new JavaSourceFallbackValidatorComparator();
        Collections.sort(fallbackValidates, comparator);

        if (_log.isDebugEnabled()) {
            StringBuilder messageBuilder = new StringBuilder();

            messageBuilder.append(RuntimeMessages.MESSAGES.multipleFallbackValidatorsAvailable());
            for (JavaSourceFallbackValidator t : fallbackValidates) {
                messageBuilder.append("\n\t- name '" + t.getValidator().getName() + "'");
            }
            _log.debug(messageBuilder.toString());
        }

        // Closest super-type will be first in the list..
        Validator<?> fallbackValidator = fallbackValidates.get(0).getValidator();
        addFallbackValidator(fallbackValidator, name);
        return fallbackValidator;
    }

    private void addFallbackValidator(Validator<?> fallbackValidator, QName name) {
        // Fallback validators are cached so as to save on lookup times...
        _fallbackValidators.put(name, fallbackValidator);
    }

    /**
     * Holder type for Fallback Validator.
     */
    public static class JavaSourceFallbackValidator {

        private Class<?> _javaType;
        private Validator<?> _validator;

        /**
         * Constructor.
         * @param javaType Java type.
         * @param validator Validator instance.
         */
        public JavaSourceFallbackValidator(Class<?> javaType, Validator<?> validator) {
            this._javaType = javaType;
            this._validator = validator;
        }

        /**
         * Get the Java type.
         * @return The Java type.
         */
        public Class<?> getJavaType() {
            return _javaType;
        }

        /**
         * Get the Validator.
         * @return The Validator.
         */
        public Validator<?> getValidator() {
            return _validator;
        }
    }

    /**
     * Fallback Validator Sorter.
     * @param <T> JavaSourceFallbackValidator.
     */
    @SuppressWarnings("serial")
    public static class JavaSourceFallbackValidatorComparator<T extends JavaSourceFallbackValidator> implements Comparator<T>, Serializable {
        @Override
        public int compare(T t1, T t2) {
            if (t1.getJavaType() == t2.getJavaType()) {
                return 0;
            } else if (t1.getJavaType().isAssignableFrom(t2.getJavaType())) {
                return 1;
            } else if (t2.getJavaType().isAssignableFrom(t1.getJavaType())) {
                return -1;
            } else {
                // Unrelated types.  This means there are branches in the inheritance options,
                // which means there are multiple possibilities, therefore it's not uniquely resolvable.
                // Marking as equal for now...
                return 0;
            }
        }
    }
}
