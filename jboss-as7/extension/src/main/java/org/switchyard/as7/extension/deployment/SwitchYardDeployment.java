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

import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.as.controller.PathElement;
import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.dmr.ModelNode;
import org.jboss.modules.Module;
import org.jboss.msc.service.ServiceController;
import org.switchyard.ServiceDomain;
import org.switchyard.admin.Application;
import org.switchyard.admin.base.BaseSwitchYard;
import org.switchyard.as7.extension.SwitchYardExtension;
import org.switchyard.as7.extension.SwitchYardModelConstants;
import org.switchyard.as7.extension.admin.ModelNodeCreationUtil;
import org.switchyard.as7.extension.services.SwitchYardAdminService;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.ActivatorLoader;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.deploy.internal.DeploymentListener;

/**
 * Represents a single AS7 deployment containing a SwitchYard application.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardDeployment {

    /** The attachment key. */
    public static final AttachmentKey<SwitchYardDeployment> ATTACHMENT_KEY = AttachmentKey
            .create(SwitchYardDeployment.class);

    private final DeploymentUnit _deployUnit;
    private SwitchYardDeploymentState _deploymentState;
    private Deployment _deployment;
    private ServiceDomainManager _domainManager;
    private ServiceDomain _appServiceDomain;

    /**
     * Creates a new SwitchYard deployment.
     *
     * @param deploymentUnit deployment reference
     * @param config switchyard configuration
     * @param domainManager Service Domain Manager instance.
     */
    public SwitchYardDeployment(final DeploymentUnit deploymentUnit, final SwitchYardModel config, ServiceDomainManager domainManager) {
        _deployUnit = deploymentUnit;
        _deployment = new Deployment(config);
        _domainManager = domainManager;
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
     * 
     * @param components The list of components configured.
     */
    public void start(final List<Component> components) {
        final Module module = _deployUnit.getAttachment(Attachments.MODULE);
        ClassLoader origCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(module.getClassLoader());
            setDeploymentState(SwitchYardDeploymentState.INITIALIZING);

            // Use the ROOT_DOMAIN name for now.  Getting an exception SwitchYardModel.getQName().
            _appServiceDomain = _domainManager.addApplicationServiceDomain(ServiceDomainManager.ROOT_DOMAIN, _deployment.getConfig());

            _deployment.init(_appServiceDomain, ActivatorLoader.createActivators(_appServiceDomain, components));
            setDeploymentState(SwitchYardDeploymentState.STARTING);
            _deployment.start();
            setDeploymentState(SwitchYardDeploymentState.STARTED);
            registerManagementNodes();
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
            try {
                final Module module = _deployUnit.getAttachment(Attachments.MODULE);
                Thread.currentThread().setContextClassLoader(module.getClassLoader());
                if (_deploymentState == SwitchYardDeploymentState.STARTED) {
                    _deployment.stop();
                    setDeploymentState(SwitchYardDeploymentState.STOPPED);
                    unregisterManagementNodes();
                }
                if (_deploymentState == SwitchYardDeploymentState.STARTING
                        || _deploymentState == SwitchYardDeploymentState.STOPPED) {
                    _deployment.destroy();
                    setDeploymentState(SwitchYardDeploymentState.DESTROYED);
                }
            } finally {
                if (_appServiceDomain != null) {
                    _domainManager.removeApplicationServiceDomain(_appServiceDomain);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(origCL);
        }
    }

    /**
     * Set the deployment state.
     * 
     * @param deploymentState the deployment state
     */
    public void setDeploymentState(SwitchYardDeploymentState deploymentState) {
        this._deploymentState = deploymentState;
    }

    /**
     * Get the deployment state.
     * 
     * @return DeploymentState
     */
    public SwitchYardDeploymentState getDeploymentState() {
        return _deploymentState;
    }

    /**
     * Pass through method for use by
     * {@link org.switchyard.as7.extension.services.SwitchYardService}.
     * 
     * @param listener the deployment listener.
     */
    public void setDeploymentListener(DeploymentListener listener) {
        _deployment.addDeploymentListener(listener);
    }

    /**
     * Pass through method for use by
     * {@link org.switchyard.as7.extension.services.SwitchYardService}.
     * 
     * @param listener the deployment listener.
     */
    public void removeDeploymentListener(DeploymentListener listener) {
        _deployment.removeDeploymentListener(listener);
    }

    private void registerManagementNodes() {
        QName applicationName = _deployment.getName();
        if (applicationName == null) {
            return;
        }

        ServiceController<?> adminService = _deployUnit.getServiceRegistry().getService(
                SwitchYardAdminService.SERVICE_NAME);
        if (adminService == null) {
            return;
        }

        BaseSwitchYard switchYard = BaseSwitchYard.class.cast(adminService.getValue());
        if (switchYard == null) {
            return;
        }

        ModelNode deployNode = _deployUnit.createDeploymentSubModel(SwitchYardExtension.SUBSYSTEM_NAME,
                PathElement.pathElement(SwitchYardModelConstants.APPLICATION, applicationName.toString()));

        Application application = switchYard.getApplication(applicationName);
        if (application == null) {
            return;
        }
        deployNode.set(ModelNodeCreationUtil.createApplicationNode(application));
    }

    private void unregisterManagementNodes() {
        QName applicationName = _deployment.getName();
        if (applicationName == null) {
            return;
        }
        _deployUnit.createDeploymentSubModel(SwitchYardExtension.SUBSYSTEM_NAME,
                PathElement.pathElement(SwitchYardModelConstants.APPLICATION, applicationName.toString())).clear();
    }

}
