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

package org.switchyard.event;

import java.util.EventObject;

import org.switchyard.validate.Validator;

/**
 * Fired when a validator is removed from the domain.
 */
public class ValidatorRemovedEvent extends EventObject {

    private static final long serialVersionUID = 7248520803400556118L;
    
    /**
     * Creates a new ValidatorRemovedEvent.
     * @param validator the validator that was removed
     */
    public ValidatorRemovedEvent(Validator<?> validator) {
        super(validator);
    }

    /**
     * Get the removed validator.
     * @return removed validator
     */
    public Validator<?> getTransformer() {
        return (Validator<?>)getSource();
    }
}
