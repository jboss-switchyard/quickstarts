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

package org.switchyard.config.model.switchyard.test.java;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.v1.V1BaseTransformModel;

/**
 * JavaTransformModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class JavaTransformModel extends V1BaseTransformModel {

    public static final String DEFAULT_NAMESPACE = "urn:switchyard-config:test-java:1.0";
    public static final String JAVA = "java";
    public static final String CLASS = "class";

    public JavaTransformModel() {
        super(new QName(DEFAULT_NAMESPACE, TransformModel.TRANSFORM + '.' + JAVA));
    }

    public JavaTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    public String getClazz() {
        return getModelAttribute(CLASS);
    }

    public JavaTransformModel setClazz(String clazz) {
        setModelAttribute(CLASS, clazz);
        return this;
    }

}
