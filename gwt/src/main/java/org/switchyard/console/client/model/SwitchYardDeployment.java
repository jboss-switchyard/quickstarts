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
package org.switchyard.console.client.model;

import java.util.List;

/**
 * SwitchYardDeployment
 * 
 * Represents a SwitchYard deployment.
 * 
 * @author Rob Cernich
 */
public interface SwitchYardDeployment {
    /**
     * @return the name of this deployment.
     */
    public String getName();

    /**
     * @param name the name of this deployment.
     */
    public void setName(String name);

    /**
     * @return the services defined within this deployment.
     */
    public List<CompositeService> getCompositeServices();

    /**
     * @param services the services defined within this deployment.
     */
    public void setCompositeServices(List<CompositeService> services);

    /**
     * @return the transforms defined within this deployment.
     */
    public List<Transform> getTransforms();

    /**
     * @param transforms the transforms defined within this deployment.
     */
    public void setTransforms(List<Transform> transforms);

    /**
     * @return the domains defined within this deployment.
     */
    public List<Domain> getDomains();

    /**
     * @param domains the domains defined within this deployment.
     */
    public void setDomains(List<Domain> domains);

    /**
     * @return the runtime name of this deployment.
     */
    public String getRuntimeName();

    /**
     * @param runtimeName the runtime name of this deployment.
     */
    public void setRuntimeName(String runtimeName);

    /**
     * @return true if this deployment is enabled.
     */
    public boolean isEnabled();

    /**
     * @param enabled true if this deployment is enabled.
     */
    public void setEnabled(boolean enabled);

}
