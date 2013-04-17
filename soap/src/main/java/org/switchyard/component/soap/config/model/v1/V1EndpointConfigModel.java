/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.soap.config.model.v1;

import static org.switchyard.component.soap.config.model.SOAPBindingModel.DEFAULT_NAMESPACE;

import org.switchyard.component.soap.config.model.EndpointConfigModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version InterceptorModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class V1EndpointConfigModel extends BaseModel implements EndpointConfigModel {

    private static final String CONFIG_FILE = "configFile";
    private static final String CONFIG_NAME = "configName";

    /**
     * Creates a new InterceptorModel.
     */
    public V1EndpointConfigModel() {
        super(ENDPOINT_CONFIG, DEFAULT_NAMESPACE);
    }

    /**
     * Creates a new EndpointConfigModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1EndpointConfigModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfigFile() {
        return getModelAttribute(CONFIG_FILE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EndpointConfigModel setConfigFile(String configFile) {
        setModelAttribute(CONFIG_FILE, configFile);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfigName() {
        return getModelAttribute(CONFIG_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EndpointConfigModel setConfigName(String configName) {
        setModelAttribute(CONFIG_NAME, configName);
        return this;
    }

}
