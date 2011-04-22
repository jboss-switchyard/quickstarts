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

package org.switchyard.deployment;

import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.internal.Deployment;

/**
 * Represents a single AS6 deployment containing a SwitchYard application.
 */
public class SwitchYardDeployment extends Deployment {
    
    private final VFSDeploymentUnit _deployUnit;

    /**
     * Creates a new SwitchYard deployment.
     * @param deploymentName name of the deployment
     * @param deploymentUnit mc deployment reference
     * @param config switchyard configuration
     */
    public SwitchYardDeployment(final String deploymentName, 
            final VFSDeploymentUnit deploymentUnit, 
            final SwitchYardModel config) {
        
        super(config);
        _deployUnit = deploymentUnit;
    }
    
    /**
     * Create the application.
     */
    public void create() {
    }

    /**
     * Destroy the application.
     */
    public void destroy() {
    }

    /**
     * Start the application.
     */
    public void start() {
        ClassLoader origCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(_deployUnit.getClassLoader());
            super.init();
            super.start();
        } finally {
            Thread.currentThread().setContextClassLoader(origCL);
        }
    }

    /**
     * Stop the application.
     */
    public void stop() {
        ClassLoader origCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(_deployUnit.getClassLoader());
            super.stop();
            super.destroy();
        } finally {
            Thread.currentThread().setContextClassLoader(origCL);
        }
    }    
}
