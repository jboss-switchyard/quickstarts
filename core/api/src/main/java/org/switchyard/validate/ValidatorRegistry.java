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
 * Registry for validators.
 */
public interface ValidatorRegistry {

    /**
     * Add a validator.
     * @param validator validator
     * @return {@code this} ValidatorRegistry instance.
     */
    ValidatorRegistry addValidator(Validator<?> validator);

    /**
     * Add a validator.
     * @param validator validator
     * @param name name
     * @return {@code this} ValidatorRegistry instance.
     */
    ValidatorRegistry addValidator(Validator<?> validator, QName name);

    /**
     * Remove a validator.
     * @param validator validatort
     * @return status of removal
     */
    boolean removeValidator(Validator<?> validator);

    /**
     * Does the registry have a validator for the specified types.
     * @param name name
     * @return True if it has a validator, otherwise false.
     */
    boolean hasValidator(QName name);

    /**
     * Get a validator.
     * @param name name
     * @return validator
     */
    Validator<?> getValidator(QName name);
}
