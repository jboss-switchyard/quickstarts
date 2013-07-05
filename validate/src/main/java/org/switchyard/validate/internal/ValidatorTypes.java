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

import javax.xml.namespace.QName;

/**
 * Validator data types.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class ValidatorTypes {

    private QName _name;

    /**
     * Public constructor.
     *
     * @param name type.
     */
    ValidatorTypes(QName name) {
        this._name = name;
    }

    /**
     * Get name.
     *
     * @return name.
     */
    public QName getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s [name=%s]", getClass().getSimpleName(), getName());
    }
}
