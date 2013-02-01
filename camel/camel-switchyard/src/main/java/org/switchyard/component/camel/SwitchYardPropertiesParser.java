/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *  *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.switchyard.component.camel;

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
