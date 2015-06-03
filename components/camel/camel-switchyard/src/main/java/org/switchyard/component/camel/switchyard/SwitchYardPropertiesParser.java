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
package org.switchyard.component.camel.switchyard;

import java.util.Properties;

import org.apache.camel.component.properties.DefaultPropertiesParser;
import org.switchyard.common.property.PropertyResolver;

/**
 * A custom Camel PropertiesParser which resolves SwitchYard implementation properties.
 */
public class SwitchYardPropertiesParser extends DefaultPropertiesParser {

    private PropertyResolver _resolver;
    
    /**
     * Constructor.
     * @param pr PropertyResolver which resolves SwitchYard implementation properties
     */
    public SwitchYardPropertiesParser(PropertyResolver pr) {
        _resolver = pr;
    }
    
     @Override
     public String parseProperty(String key, String value, Properties properties) {
         if (_resolver != null) {
             Object response = _resolver.resolveProperty(key);
             if (response != null) {
                 return response.toString();
             }
         }
         
         return super.parseProperty(key, value, properties);
     }
}
