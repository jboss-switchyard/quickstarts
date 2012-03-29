/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
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
