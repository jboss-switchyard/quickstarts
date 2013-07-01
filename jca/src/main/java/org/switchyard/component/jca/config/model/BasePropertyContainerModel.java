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
package org.switchyard.component.jca.config.model;

import java.util.Properties;

import org.switchyard.config.model.Model;

/**
 * base property container model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public interface BasePropertyContainerModel extends Model {

    /**
     * get property value.
     * 
     * @param key key of property to get
     * @return value of property
     */
    String getProperty(String key);
    
    /**
     * set property model.
     * 
     * @param key key of property to set
     * @param value value of property to set
     * @return {@ResourceAdapterModel} to support method chaining
     */
    BasePropertyContainerModel setProperty(String key, String value);
    
    /**
     * get all properties as {@link Properties} object.
     * 
     * @return {@link Properties}
     */
    Properties getProperties();
    
    /**
     * set Properties object.
     * 
     * @param properties {@link Properties} to set
     * @return {@link BasePropertyContainerModel} to support method chaining
     */
    BasePropertyContainerModel setProperties(final Properties properties);
}
