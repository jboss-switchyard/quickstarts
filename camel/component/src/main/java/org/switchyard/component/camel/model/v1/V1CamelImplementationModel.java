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
package org.switchyard.component.camel.model.v1;

import org.switchyard.component.camel.common.model.v1.NameValueModel;
import org.switchyard.component.camel.model.CamelComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1ComponentImplementationModel;

/**
 * Version 1 implementation.
 * 
 * @author Daniel Bevenius
 */
public class V1CamelImplementationModel extends V1ComponentImplementationModel
    implements CamelComponentImplementationModel {

    // The class attribute for Java DSL routes
    private static final String CLASS = "class";

    // The path attribute for XML DSL routes
    private static final String PATH = "path";

    /**
     * Create a new CamelImplementationModel.
     * @param namespace namespace
     */
    public V1CamelImplementationModel(String namespace) {
        super(CAMEL, namespace);
    }

    /**
     * Create a CamelImplementationModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder();
    }

    @Override
    public String getJavaClass() {
        Configuration classConfig = getModelConfiguration().getFirstChild(JAVA);
        return classConfig != null ? classConfig.getAttribute(CLASS) : null;
    }

    @Override
    public V1CamelImplementationModel setJavaClass(String className) {
        Configuration classConfig = getModelConfiguration().getFirstChild(JAVA);
        if (classConfig == null) {
            NameValueModel model = new NameValueModel(getNamespaceURI(), JAVA);
            model.getModelConfiguration().setAttribute(CLASS, className);
            setChildModel(model);
        } else {
            classConfig.setAttribute(CLASS, className);
        }
        return this;
    }

    @Override
    public String getXMLPath() {
        Configuration classConfig = getModelConfiguration().getFirstChild(XML);
        return classConfig != null ? classConfig.getAttribute(PATH) : null;
    }

    @Override
    public CamelComponentImplementationModel setXMLPath(String path) {
        Configuration pathConfig = getModelConfiguration().getFirstChild(XML);
        if (pathConfig == null) {
            NameValueModel model = new NameValueModel(getNamespaceURI(), XML);
            model.getModelConfiguration().setAttribute(PATH, path);
            setChildModel(model);
        } else {
            pathConfig.setAttribute(PATH, path);
        }
        return this;
    }

}
