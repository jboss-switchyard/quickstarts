/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
