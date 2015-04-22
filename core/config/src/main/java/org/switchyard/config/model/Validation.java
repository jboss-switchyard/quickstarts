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
package org.switchyard.config.model;

import org.switchyard.config.ConfigMessages;

/**
 * Represents the result of a Model validation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Validation {

    private Class<? extends Model> _modelClass;
    private boolean _valid;
    private String _message;
    private Throwable _cause;

    /**
     * Constructs a new Validation.
     * @param modelClass the model class
     * @param valid whether or not the validation was a success
     */
    public Validation(Class<? extends Model> modelClass, boolean valid) {
        this(modelClass, valid, null);
    }

    /**
     * Constructs a new Validation.
     * @param modelClass the model class
     * @param valid whether or not the validation was a success
     * @param message the message to report
     */
    public Validation(Class<? extends Model> modelClass, boolean valid, String message) {
        _modelClass = modelClass;
        _valid = valid;
        _message = message;
    }

    /**
     * Constructs a new, unsuccessful Validation.
     * @param modelClass the model class
     * @param cause the cause of the failed validation
     */
    public Validation(Class<? extends Model> modelClass, Throwable cause) {
        this(modelClass, false, cause.getMessage());
        _cause = cause;
    }

    /**
     * Gets the class of the model which underwent validation.
     * @return the class of the model which underwent validation
     */
    public Class<? extends Model> getModelClass() {
        return _modelClass;
    }

    /**
     * Asserts this validation was successful.
     * @return this Validation (useful for chaining)
     */
    public Validation assertValid() {
        if (!isValid()) {
            throw ConfigMessages.MESSAGES.modelInvalid(getModelClass().getName(), getMessage(), getCause());
        }
        return this;
    }

    /**
     * Whether this validation was successful.
     * @return true if valid
     */
    public boolean isValid() {
        return _valid;
    }

    /**
     * Gets the message to report.
     * @return the message
     */
    public String getMessage() {
        return _message;
    }

    /**
     * Gets the cause of a failed validation.
     * @return the cause
     */
    public Throwable getCause() {
        return _cause;
    }

}
