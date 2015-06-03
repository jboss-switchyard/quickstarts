package org.switchyard.validate.internal;
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



import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.validate.Validator;

/**
 * Validator Factory.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 * @param <T> ValidatorModel type.
 */
public interface ValidatorFactory<T extends ValidateModel> {

    /**
     * Create a new {@link Validator} instance.
     * @param model The Validator config model.
     * @return The Validator instance.
     */
    Validator<?> newValidator(T model);
}
