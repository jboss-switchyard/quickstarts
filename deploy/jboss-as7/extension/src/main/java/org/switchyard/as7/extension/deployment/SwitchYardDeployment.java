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
package org.switchyard.as7.extension.deployment;

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.modules.Module;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.Deployment;

/**
 * Represents a single AS7 deployment containing a SwitchYard application.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardDeployment extends Deployment {

    /** The attachment key. */
    public static final AttachmentKey<SwitchYardDeployment> ATTACHMENT_KEY = AttachmentKey.create(SwitchYardDeployment.class);

    private final DeploymentUnit _deployUnit;
    private SwitchYardDeploymentState _deploymentState;
 
    /**
     * Creates a new SwitchYard deployment.
     * @param deploymentUnit deployment reference
     * @param config switchyard configuration
     */
    public SwitchYardDeployment(final DeploymentUnit deploymentUnit, final SwitchYardModel config) {
        
        super(ServiceDomainManager.createDomain(), config);
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
        final Module module = _deployUnit.getAttachment(Attachments.MODULE);
        ClassLoader origCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(module.getClassLoader());
            setDeploymentState(SwitchYardDeploymentState.INITIALIZING);
            super.init();
            setDeploymentState(SwitchYardDeploymentState.STARTING);
            super.start();
            setDeploymentState(SwitchYardDeploymentState.STARTED);
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
            final Module module = _deployUnit.getAttachment(Attachments.MODULE);
            Thread.currentThread().setContextClassLoader(module.getClassLoader());
            if (_deploymentState == SwitchYardDeploymentState.STARTED) {
                super.stop();
                setDeploymentState(SwitchYardDeploymentState.STOPPED);
            }
            if (_deploymentState == SwitchYardDeploymentState.STARTING
                   || _deploymentState == SwitchYardDeploymentState.STOPPED) {
                super.destroy();
                setDeploymentState(SwitchYardDeploymentState.DESTROYED);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(origCL);
        }
    }

    /**
     * Set the deployment state.
     * @param deploymentState the deployment state
     */
    public void setDeploymentState(SwitchYardDeploymentState deploymentState) {
        this._deploymentState = deploymentState;
    }

    /**
     * Get the deployment state.
     * @return DeploymentState
     */
    public SwitchYardDeploymentState getDeploymentState() {
        return _deploymentState;
    }
}
