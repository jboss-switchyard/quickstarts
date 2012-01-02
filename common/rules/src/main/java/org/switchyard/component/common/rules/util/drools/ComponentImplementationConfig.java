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
package org.switchyard.component.common.rules.util.drools;

import java.util.Map;
import java.util.Properties;

import org.switchyard.component.common.rules.config.model.ComponentImplementationModel;

/**
 * Component implementation config information.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ComponentImplementationConfig {

    private ComponentImplementationModel _model;
    private ClassLoader _loader;
    private Map<String, Object> _environmentOverrides;
    private Properties _propertiesOverrides;

    /**
     * Public default constructor.
     */
    public ComponentImplementationConfig() {}

    /**
     * Public constructor tasking a component implementation model.
     * @param model the component implementation model
     */
    public ComponentImplementationConfig(ComponentImplementationModel model) {
        setModel(model);
    }

    /**
     * Public constructor tasking a component implementation model and a classloader.
     * @param model the component implementation model
     * @param loader the classloader
     */
    public ComponentImplementationConfig(ComponentImplementationModel model, ClassLoader loader) {
        setModel(model);
        setLoader(loader);
    }

    /**
     * Gets the model.
     * @return the model
     */
    public ComponentImplementationModel getModel() {
        return _model;
    }

    /**
     * Sets the model.
     * @param model the model
     * @return this instance
     */
    public ComponentImplementationConfig setModel(ComponentImplementationModel model) {
        _model = model;
        return this;
    }

    /**
     * Gets the classloader.
     * @return the classloader
     */
    public ClassLoader getLoader() {
        return _loader;
    }

    /**
     * Sets the classloader.
     * @param loader the classloader
     * @return this instance
     */
    public ComponentImplementationConfig setLoader(ClassLoader loader) {
        _loader = loader;
        return this;
    }

    /**
     * Gets the environment overrides.
     * @return the environment overrides
     */
    public Map<String, Object> getEnvironmentOverrides() {
        return _environmentOverrides;
    }

    /**
     * Sets the environment overrides.
     * @param environmentOverrides the environment overrides
     * @return this instance
     */
    public ComponentImplementationConfig setEnvironmentOverrides(Map<String, Object> environmentOverrides) {
        _environmentOverrides = environmentOverrides;
        return this;
    }

    /**
     * Gets the properties overrides.
     * @return the properties overrides
     */
    public Properties getPropertiesOverrides() {
        return _propertiesOverrides;
    }

    /**
     * Sets the properties overrides.
     * @param propertiesOverrides the properties overrides
     * @return this instance
     */
    public ComponentImplementationConfig setPropertiesOverrides(Properties propertiesOverrides) {
        _propertiesOverrides = propertiesOverrides;
        return this;
    }

}
