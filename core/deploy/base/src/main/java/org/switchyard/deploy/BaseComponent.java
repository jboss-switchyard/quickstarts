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
package org.switchyard.deploy;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;

/**
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public abstract class BaseComponent implements Component {

    private String _name;
    private Configuration _config = Configurations.newConfiguration();
    private List<String> _activationTypes = new LinkedList<String>();

    protected BaseComponent(String ... types) {
        if (types != null) {
            _activationTypes.addAll(Arrays.asList(types));
        }
    }

    /* (non-Javadoc)
     * @see org.switchyard.deploy.Component#getName()
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * Sets the name of this component.
     *
     * @param name the name to set
     * @see org.switchyard.deploy.Component#getName()
     */
    public void setName(String name) {
        _name = name;
    }

    /* (non-Javadoc)
     * @see org.switchyard.deploy.Component#init(org.switchyard.config.Configuration)
     */
    @Override
    public void init(Configuration config) {
        _config = config;
    }

    /**
     * Returns the configuration for this component.
     *
     * @return the configuration used to initialize this component
     * @see org.switchyard.deploy.Component#init(org.switchyard.config.Configuration)
     */
    public Configuration getConfig() {
        return _config;
    }
    
    @Override
    public List<String> getActivationTypes() {
        return Collections.unmodifiableList(_activationTypes);
    }

    /* (non-Javadoc)
     * @see org.switchyard.deploy.Component#destroy()
     */
    @Override
    public void destroy() {
    }

    @Override
    public void addResourceDependency(Object value) {
        // ignore by default
    }
}
