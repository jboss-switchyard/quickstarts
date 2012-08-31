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
    private Configuration _config = Configurations.emptyConfig();
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
