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
package org.switchyard.component.hornetq.config.model;

import org.switchyard.config.model.Model;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.PropertyModel;

/**
 * A HornetQConnectorConfigModel represents a connector configuration in HornetQ which
 * define how clients connect to a HornetQ Server. 
 * 
 * @author Daniel Bevenius
 *
 */
public interface HornetQConnectorConfigModel extends Model {
    
    /**
     * The name of this configuration element.
     */
    String CONNECTOR = "connector";
    
    /**
     * The optional name for this connector.
     */
    String NAME_ATTRIBUTE = "name";
    
    /**
     * The factory class that can create the connector.
     */
    String FACTORY_CLASS_ELEMENT = "factoryClass";
    
    /**
     * Returns the connector name if one was specified.
     * 
     * @return String the connector name, or null if none was configured.
     */
    String getConnectorName();
    
    /**
     * Sets the connector name.
     * 
     * @param name the optional connector name.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConnectorConfigModel setConnectorName(String name);
    
    /**
     * Gets the connector class name.
     * 
     * @return String the connector class name.
     */
    String getConnectorClassName();
    
    /**
     * Sets the connector name.
     * 
     * @param className the optional connector name.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConnectorConfigModel setConnectorClassName(String className);
    
    /**
     * Adds a property.
     * 
     * @param property a property for this connector configuration.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConnectorConfigModel addProperty(PropertyModel property);
    
    /**
     * Gets all the properties configured for this connector.
     * 
     * @return {@link PropertiesModel} the model containing all the configured properties for this connector config.
     */
    PropertiesModel getProperties();

}
