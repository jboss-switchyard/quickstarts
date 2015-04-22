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
package org.switchyard.config.model.validate;

import java.util.List;

import org.switchyard.config.model.Model;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * The "validates" configuration model.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public interface ValidatesModel extends Model {

    /** The "validates" name. */
    public static final String VALIDATES = "validates";

    /**
     * Gets the parent switchyard model.
     * @return the parent switchyard model
     */
    public SwitchYardModel getSwitchYard();

    /**
     * Gets the child validate models.
     * @return the child validate models
     */
    public List<ValidateModel> getValidates();

    /**
     * Adds a child validate model.
     * @param validate the child validate model to add
     * @return this ValidatesModel (useful for chaining)
     */
    public ValidatesModel addValidate(ValidateModel validate);

}
