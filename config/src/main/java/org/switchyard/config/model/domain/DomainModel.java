/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
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
package org.switchyard.config.model.domain;

import java.util.Map;

import org.switchyard.config.model.NamedModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * The "domain" configuration model.
 */
public interface DomainModel extends NamedModel {

    /** The default "domain" namespace. */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-config:domain:1.0";
    
    /** The "domain" name. :-) */
    public static final String DOMAIN = "domain";
    
    /**
     * Gets the parent switchyard model.
     * @return the parent switchyard model
     */
    public SwitchYardModel getSwitchYard();
    
    /**
     * Gets the child property value.
     * @param name the property name
     * @return the child property value, or null if the property does not exist
     */
    public String getProperty(String name);
    
    /**
     * Sets the child property value.
     * @param name property name
     * @param value property value
     * @return this DomainModel (useful for chaining)
     */
    public DomainModel setProperty(String name, String value);
    
    /**
     * Returns a map representation of domain properties, with property name
     * as the map key and property value as a map entry's value.
     * @return property map
     */
    public Map<String, String> getProperties();
}
