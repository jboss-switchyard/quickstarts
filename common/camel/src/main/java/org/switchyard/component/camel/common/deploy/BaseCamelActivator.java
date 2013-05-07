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
package org.switchyard.component.camel.common.deploy;

import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.config.Configuration;
import org.switchyard.deploy.BaseActivator;

/**
 * Activator for handling camel bindings on both, service and reference, sides.
 * 
 * @author Lukasz Dywicki
 */
public class BaseCamelActivator extends BaseActivator {

    private Configuration _environment;
    private SwitchYardCamelContext _camelContext;

    protected BaseCamelActivator(SwitchYardCamelContext context, String ... types) {
        super(types);
        _camelContext = context;
    }

    /**
     * Specify environment configuration for binding.
     * 
     * @param config Environment settings.
     */
    public void setEnvironment(Configuration config) {
        _environment = config;
    }

    /**
     * Gets the {@ling Configuration} used by this Activator.
     * 
     * @return Configuration of the Activator.
     */
    public Configuration getEnvironment() {
        return _environment;
    }

    /**
     * Gets the {@link SwitchYardCamelContext} used by this Activator.
     * 
     * @return The CamelContext used by this Activator.
     */
    public SwitchYardCamelContext getCamelContext() {
        return _camelContext;
    }

}
