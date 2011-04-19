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
package org.switchyard.component.camel.config.model;

import org.apache.camel.model.RouteDefinition;
import org.switchyard.config.model.composite.ComponentImplementationModel;

/**
 * Definition of a Camel implementation model (implementation.camel).
 * </p> 
 * This allows for the usage of Camel DSL for routing between SwichYard
 * Services.
 * 
 * @author Daniel Bevenius
 *
 */
public interface CamelComponentImplementationModel extends ComponentImplementationModel {
    
    /**
     * The names space for the camel config model.
     */
    String DEFAULT_NAMESPACE = "urn:switchyard-component-camel:config:1.0";
    
    /**
     * The 'camel' implementation type.
     */
    String CAMEL = "camel";
    
    /**
     * The name of the Camel route element.
     */
    String ROUTE = "route";
    
    /**
     * The Camel route definition.
     * @return {@link RouteDefinition} The Camel {@link RouteDefinition}.
     */
    RouteDefinition getRoute();
    
}
