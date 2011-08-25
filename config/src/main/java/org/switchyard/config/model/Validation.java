/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.config.model;

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
            throw new RuntimeException("Model [" + getModelClass().getName() + "] is invalid: " + getMessage(), getCause());
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
