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
package org.switchyard.component.jca.config.model.v1;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.jca.config.model.BasePropertyContainerModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * V1 BasePropertyContainer model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public abstract class V1BasePropertyContainerModel extends BaseModel implements BasePropertyContainerModel {

    protected V1BasePropertyContainerModel(String namespace, String name) {
        super(namespace, name);
    }
    
    protected V1BasePropertyContainerModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getProperty(String key) {
        List<Configuration> properties = getModelConfiguration().getChildren(JCAConstants.PROPERTY);
        for (Configuration prop : properties) {
            if (key.equals(prop.getAttribute(JCAConstants.NAME))) {
                return prop.getAttribute(JCAConstants.VALUE);
            }
        }
        return null;
    }

    @Override
    public BasePropertyContainerModel setProperty(String key, String value) {
        List<Configuration> properties = getModelConfiguration().getChildren(JCAConstants.PROPERTY);
        for (Configuration prop : properties) {
            if (key.equals(prop.getAttribute(JCAConstants.NAME))) {
                prop.setAttribute(JCAConstants.VALUE, value != null ? value : "");
                return this;
            }
        }
        V1PropertyModel model = new V1PropertyModel(getNamespaceURI(), key, value != null ? value : "");
        setChildModel(model);
        return this;
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        List<Configuration> modelProperties = getModelConfiguration().getChildren(JCAConstants.PROPERTY);
        for (Configuration prop : modelProperties) {
            String name = prop.getAttribute(JCAConstants.NAME);
            String value = prop.getAttribute(JCAConstants.VALUE);
            if (name != null) {
                properties.put(name, value != null ? value : "");
            }
        }
        return properties;
    }

    @Override
    public BasePropertyContainerModel setProperties(Properties properties) {
        getModelConfiguration().removeChildren(JCAConstants.PROPERTY);
        Enumeration<?> e = properties.keys();
        while (e.hasMoreElements()) {
            String key = e.nextElement().toString();
            String value = properties.getProperty(key);
            V1PropertyModel model = new V1PropertyModel(getNamespaceURI(), key, value);
            setChildModel(model);
        }
        return this;
    }

}
