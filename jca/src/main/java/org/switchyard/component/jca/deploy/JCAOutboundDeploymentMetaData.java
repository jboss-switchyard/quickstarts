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
package org.switchyard.component.jca.deploy;

import java.util.Properties;

/**
 * JCA outbound deployment metadata.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class JCAOutboundDeploymentMetaData {

    private Class<?> _connectionFactoryClass;
    private String _connectionFactoryJNDIName;
    private Properties _connectionProperties;
    private Object _connectionSpec;
    private Object _interactionSpec;
    
    /**
     * Set connection factory class.
     * 
     * @param cfClass connection factory class 
     * @return {@link JCAOutboundDeploymentMetaData} to support method chaining
     */
    public JCAOutboundDeploymentMetaData setConnectionFactoryClass(Class<?> cfClass) {
        _connectionFactoryClass = cfClass;
        return this;
    }

    /**
     * Get connection factory class.
     * @return connection factory class
     */
    public Class<?> getConnectionFactoryClass() {
        return _connectionFactoryClass;
    }
    
    /**
     * Set connection factory JNDI name.
     * 
     * @param cfJndiName connection factory JNDI name
     * @return {@link JCAOutboundDeploymentMetaData} to support method chaining
     */
    public JCAOutboundDeploymentMetaData setConnectionFactoryJNDIName(String cfJndiName) {
        _connectionFactoryJNDIName = cfJndiName;
        return this;
    }
    
    /**
     * Get connection factory JNDI name.
     * 
     * @return connection factory JNDI name
     */
    public String getConnectionFactoryJNDIName() {
        return _connectionFactoryJNDIName;
    }

    /**
     * Set connection properties.
     * 
     * @param props connection properties
     * @return {@link JCAOutboundDeploymentMetaData} to support method chaining
     */
    public JCAOutboundDeploymentMetaData setConnectionProperties(Properties props) {
        _connectionProperties = props;
        return this;
    }

    /**
     * Get connection properties.
     * @return connection properties
     */
    public Properties getConnectionProperties() {
        return _connectionProperties;
    }

    /**
     * Set connection spec.
     * 
     * @param connSpec connection spec
     * @return {@link JCAOutboundDeploymentMetaData} to support method chaining
     */
    public JCAOutboundDeploymentMetaData setConnectionSpec(Object connSpec) {
        _connectionSpec = connSpec;
        return this;
    }

    /**
     * Get connection spec.
     * 
     * @return connection spec
     */
    public Object getConnectionSpec() {
        return _connectionSpec;
    }

    /**
     * Set interaction spec.
     * 
     * @return interaction spec
     */
    public Object getInteractionSpec() {
        return _interactionSpec;
    }

    /**
     * Get interaction spec.
     * 
     * @param interactSpec interaction spec
     * @return {@link JCAOutboundDeploymentMetaData} to support method chaining
     */
    public JCAOutboundDeploymentMetaData setInteractionSpec(Object interactSpec) {
        _interactionSpec = interactSpec;
        return this;
    }
}
