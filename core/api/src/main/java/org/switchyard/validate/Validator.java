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

import javax.xml.namespace.QName;

/**
 * Handles validation for message content to ensure type and structure is
 * suitable for consumer. A Validator instance can be directly attached to
 * an exchange or it can be registered in the ValidatorRegistry and loaded
 * dynamically based on the message name.
 *
 * @param <T> Java type representing the content to be validated
 */
public interface Validator<T> {

    /**
     * Validates the content.
     * <code>T</code>.
     * @param content the content to be validated
     * @return true if the content is valid, otherwise false
     */
    ValidationResult validate(T content);

    /**
     * Set the name of the subject for validation.
     * @param name name of the subject for validation.
     * @return a reference to the current Validator.
     */
    Validator<T> setName(QName name);

    /**
     * Return the name of the subject for validation.
     * @return from message
     */
    QName getName();

    /**
     * Return the Java type of the content to be validated.
     * @return class representing the Java type for the content to be validated
     */
    Class<T> getType();
}
