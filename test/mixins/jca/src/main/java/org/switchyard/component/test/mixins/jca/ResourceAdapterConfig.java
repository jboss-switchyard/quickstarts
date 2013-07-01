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
package org.switchyard.component.test.mixins.jca;

import java.util.HashMap;
import java.util.Map;

/**
 * ResourceAdapter configuration for each ResourceAdapter to be deployed by JCAMixIn.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class ResourceAdapterConfig {
    /** ResourceAdapter type. */
    public enum ResourceAdapterType {MOCK, HORNETQ, OTHER};
    
    private ResourceAdapterType _type;
    private String _name;
    
    private Map<String, String> _connectionDefinitions = new HashMap<String, String>();
    
    /**
     * Constructor.
     * 
     * @param type ResourceAdapter type
     */
    public ResourceAdapterConfig(ResourceAdapterType type) {
        _type = type;
    }
    
    /**
     * get ResourceAdapter type.
     * 
     * @return ResourceAdapter type
     */
    public ResourceAdapterType getType() {
        return _type;
    }
    
    /**
     * add connection definition.
     * 
     * @param jndi JNDI name to bind
     * @param cfClass FQN of ManagedConnectionFactory implementation class
     * @return ResourceAdapterConfig to suport method chaining
     */
    public ResourceAdapterConfig addConnectionDefinition(String jndi, String cfClass) {
        _connectionDefinitions.put(jndi, cfClass);
        return this;
    }
    
    /**
     * get connection definitions.
     * 
     * @return connection definitions
     */
    public Map<String, String> getConnectionDefinitions() {
        return _connectionDefinitions;
    }
    
    /**
     * get ResourceAdapter name.
     * 
     * @return name
     */
    public String getName() {
        return _name;
    }
    
    /**
     * set ResourceAdapter name.
     * 
     * @param name name
     * @return ResourceAdapterConfig to support method chaining.
     */
    public ResourceAdapterConfig setName(String name) {
        _name = name;
        return this;
    }
}
