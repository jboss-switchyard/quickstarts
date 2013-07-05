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

package org.switchyard.config.model.switchyard.test.validate.java;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.v1.V1BaseValidateModel;

/**
 * JavaTransformModel.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class JavaValidateModel extends V1BaseValidateModel {

    public static final String DEFAULT_NAMESPACE = "urn:switchyard-config:test-validate-java:1.0";
    public static final String JAVA = "java";
    public static final String CLASS = "class";

    public JavaValidateModel() {
        super(new QName(DEFAULT_NAMESPACE, ValidateModel.VALIDATE + '.' + JAVA));
    }

    public JavaValidateModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    public String getClazz() {
        return getModelAttribute(CLASS);
    }

    public JavaValidateModel setClazz(String clazz) {
        setModelAttribute(CLASS, clazz);
        return this;
    }

}
