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
