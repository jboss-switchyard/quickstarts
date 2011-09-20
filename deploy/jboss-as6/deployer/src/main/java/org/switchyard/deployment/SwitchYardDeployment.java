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

import java.util.List;

import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.switchyard.ServiceDomain;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.ActivatorLoader;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.Deployment;

/**
 * Represents a single AS6 deployment containing a SwitchYard application.
 */
public class SwitchYardDeployment {
    
    private final VFSDeploymentUnit _deployUnit;
    private ServiceDomainManager _domainManager;
    private Deployment _deployment;

    /**
     * Creates a new SwitchYard deployment.
     * @param deploymentName name of the deployment
     * @param deploymentUnit mc deployment reference
     * @param config switchyard configuration
     * @param domainManager The domain manager instance.
     */
    public SwitchYardDeployment(final String deploymentName, 
            final VFSDeploymentUnit deploymentUnit, 
            final SwitchYardModel config,
            final ServiceDomainManager domainManager) {
        
        _deployUnit = deploymentUnit;
        _domainManager = domainManager;
        _deployment = new Deployment(config);
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
        _domainManager.removeApplicationServiceDomain(_deployment.getDomain());
    }

    /**
     * Start the application.
     */
    public void start() {
        ClassLoader origCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(_deployUnit.getClassLoader());
            ServiceDomain domain = createDomain(_deployment.getConfig(), _domainManager);
            List<Activator> activators = ActivatorLoader.createActivators(domain, (List<Component>) _deployUnit.getAttachment("components"));
            _deployment.init(domain, activators);
            _deployment.start();
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
            _deployment.stop();
            _deployment.destroy();
        } finally {
            Thread.currentThread().setContextClassLoader(origCL);
        }
    }

    private static ServiceDomain createDomain(SwitchYardModel config, ServiceDomainManager domainManager) {
        // Use the ROOT_DOMAIN name for now.  Getting an exception SwitchYardModel.getQName().
        return domainManager.addApplicationServiceDomain(ServiceDomainManager.ROOT_DOMAIN, config);
    }
}
