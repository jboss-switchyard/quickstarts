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

package org.switchyard.validate;

import java.lang.reflect.ParameterizedType;

import javax.xml.namespace.QName;

import org.switchyard.metadata.JavaTypes;

/**
 * Base validator implementation.
 *
 * @param <T> Type
 */
public abstract class BaseValidator<T> implements Validator<T> {

    private QName _name;

    /**
     * Constructor.
     */
    public BaseValidator() {
        _name = JavaTypes.toMessageType(getType());
    }

    /**
     * Constructor.
     * @param name name
     */
    public BaseValidator(QName name) {
        _name = name;
    }

    @Override
    public Validator<T> setName(QName name) {
        _name = name;
        return this;
    }

    @Override
    public QName getName() {
        return _name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getType() {
        try {
            ParameterizedType pt =
                (ParameterizedType) getClass().getGenericSuperclass();

            return (Class<T>) pt.getActualTypeArguments()[0];
        } catch (Exception e) {
            // Generics not specified...
            return (Class<T>) Object.class;
        }
    }

    /**
     * Create validation result which indicates valid result.
     * @return valid result
     */
    public static ValidationResult validResult() {
        return new DefaultValidationResult(true, null);
    }
    
    /**
     * Create validation result which indicates invalid result.
     * @param error error message
     * @return invalid result
     */
    public static ValidationResult invalidResult(String error) {
        return new DefaultValidationResult(false, error);
    }
    
    /**
     * Create validation result which indicates invalid result.
     * @return invalid result
     */
    public static ValidationResult invalidResult() {
        return invalidResult(null);
    }
    
    /**
     * Get the type QName for the specified Java type.
     * <p/>
     *
     * @param type The Java type.
     * @return  The QName type.
     */
    protected static QName toMessageType(Class<?> type) {
        return JavaTypes.toMessageType(type);
    }

    protected static class DefaultValidationResult implements ValidationResult {
        private boolean _valid;
        private String _detail;

        public DefaultValidationResult(boolean valid, String detail) {
            _valid = valid;
            _detail = detail;
        }
        
        public boolean isValid() {
            return _valid;
        }
        
        public String getDetail() {
            return _detail;
        }
    }
    
}
