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
package org.switchyard.admin.base;

import javax.xml.namespace.QName;

import org.switchyard.admin.Validator;
import org.switchyard.config.model.TypedModel;
import org.switchyard.config.model.validate.ValidateModel;

/**
 * BaseValidator
 * 
 * Base implementation for {@link Validator}.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class BaseValidator implements Validator {

    private final QName _name;
    private String _type;

    /**
     * Create a new BaseTransformer.
     * 
     * @param name the name of type
     * @param type the implementation type (e.g. java)
     */
    public BaseValidator(QName name, String type) {
        _name = name;
        _type = type;
    }

    /**
     * Create a new BaseValidator from a config model.
     * @param config the the validator config model
     */
    public BaseValidator(ValidateModel config) {
        _name = config.getName();
        if (config instanceof TypedModel) {
            _type = ((TypedModel)config).getType();
        }
    }

    @Override
    public QName getName() {
        return _name;
    }

    @Override
    public String getType() {
        return _type;
    }

}
