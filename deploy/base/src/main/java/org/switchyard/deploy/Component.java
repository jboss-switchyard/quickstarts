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

import org.switchyard.ServiceDomain;
import org.switchyard.config.Configuration;

/**
 * Components allow components to participate in the deployment and configuration lifecycle of
 * a SwitchYard runtime. Components are initialized during boot strap process at system startup.
 * There is exactly one instance of a particular SwitchYard Component per SwitchYard runtime.
 */
public interface Component {
    /**
     * Creates or configures an Activator associated with this Component.
     * Component instances should attempt to use the domain passed
     * to configure the Activator associated with them.
     * @param domain The SwitchYard Service Domain.
     * @return An Activator instance that will be used by deployments.
     */
    Activator getActivator(ServiceDomain domain);
    /**
     * Returns the name of the Component instance.
     * @return The name.
     */
    String getName();
    /**
     * Initialize a component based on the supplied environment/global
     * configuration. Component instances should attempt to use the 
     * configuration passed when necessary to configure the Activator
     * associated with them.
     * @param config switchyard environment/global configuration for the component
     */
    void init(Configuration config);
    /**
     * Destroy the specified Component. Component implementations should
     * use this lifecyle method to clean up any resources used by Activators.
     */
    void destroy();
    
    /**
     * @return the configuration used by this component.
     * @see #init(Configuration)
     */
    Configuration getConfig();
}
