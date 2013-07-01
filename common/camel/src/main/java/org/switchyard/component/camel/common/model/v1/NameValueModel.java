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
package org.switchyard.component.camel.common.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;

/**
 * Generic configuration interface used to represent a basic name to value mapping
 * for a child configuration, e.g.
 * <br><br>
 * <pre>
 *    <name>value</name>
 * </pre>
 * <br>
 */
public class NameValueModel extends BaseModel {

    /**
     * Create a new NameValueModel with the specified name.
     * 
     * @param namespace Config namespace.
     * @param name Config name
     */
    public NameValueModel(String namespace, String name) {
        super(new QName(namespace, name));
    }

    /**
     * Create a new NameValueModel based on an existing config element.
     * @param config configuration element
     */
    public NameValueModel(Configuration config) {
        super(config);
    }

    /**
     * Get the config value.
     * @return config value
     */
    public String getValue() {
        return super.getModelValue();
    }

    /**
     * Set the config value.
     * @param value config value
     */
    public void setValue(String value) {
        super.setModelValue(value);
    }

}
