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

import org.switchyard.SwitchYardException;

/**
 * A DuplicateValidatorException is thrown by SwitchYard when a duplicate validator
 * is trying to be registered for a 'name' type for which there already exists 
 * a validator in the validator registry.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class DuplicateValidatorException extends SwitchYardException {

    /**
     * Serial version unique id.
     */
    private static final long serialVersionUID = -8937972965502786027L;

    /**
     * Public constructor.
     * @param message Exception message.
     */
    public DuplicateValidatorException(final String message) {
        super(message);
    }

    /**
     * Public constructor.
     * @param message Exception message.
     * @param cause Throwable cause.
     */
    public DuplicateValidatorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
