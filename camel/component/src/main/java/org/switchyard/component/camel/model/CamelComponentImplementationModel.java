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
package org.switchyard.component.camel.model;

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
     * The name of the Camel java element.
     */
    String JAVA = "java";

    /**
     * The name of the Camel xml element.
     */
    String XML = "xml";
    
    /**
     * Name of a Class containing one or more @Route definitions.
     * @return reference to the route class
     */
    String getJavaClass();
    
    /**
     * Path to a file containing an XML route definition.
     * @return path to the route definition
     */
    String getXMLPath();
    
    /**
     * Set the name of a Class with one ore more @Route definitions.
     * @param className name of the class
     * @return reference to this config model
     */
    CamelComponentImplementationModel setJavaClass(String className);
    
    /**
     * Set the path to a file containing an XML route definition.
     * @param path path to a route definition
     * @return reference to this config model
     */
    CamelComponentImplementationModel setXMLPath(String path);
    
}
