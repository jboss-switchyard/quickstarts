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
package org.switchyard.as7.extension.deployment;

import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.as.controller.PathElement;
import org.jboss.as.naming.context.NamespaceContextSelector;
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
import org.switchyard.as7.extension.camel.JBossThreadPoolFactory;
import org.switchyard.as7.extension.camel.NamespaceContextPolicy;
import org.switchyard.as7.extension.services.SwitchYardAdminService;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.ActivatorLoader;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.Deployment;

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
    private NamespaceContextSelector _contextSelector;

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
     * Set by SwitchYardService before start() is called to allow the namespace
     * context to be set for any threads created in the application.
     * @param contextSelector NamespaceContextSelector
     */
    public void setNamespaceContextSelector(NamespaceContextSelector contextSelector) {
        _contextSelector = contextSelector;
    }

    /**
     * Start the application.
     * 
     * @param components the list of components
     */
    public void start(final List<Component> components) {
        final Module module = _deployUnit.getAttachment(Attachments.MODULE);
        ClassLoader origCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(module.getClassLoader());
            setDeploymentState(SwitchYardDeploymentState.INITIALIZING);

            _appServiceDomain = _domainManager.createDomain(getName(_deployment.getConfig()), _deployment.getConfig());

            // Override the default Camel ThreadPoolFactory to allow for naming
            // context to be set on any threads created within Camel
            SwitchYardCamelContext camelCtx = (SwitchYardCamelContext)
                    _appServiceDomain.getProperty(SwitchYardCamelContext.CAMEL_CONTEXT_PROPERTY);
            camelCtx.getExecutorServiceManager().setThreadPoolFactory(
                    new JBossThreadPoolFactory(_contextSelector));

            // Configure policy ref which can be used for Camel routes which may
            // not execute on camel threads (e.g. quartz)
            camelCtx.getWritebleRegistry().put(
                    NamespaceContextPolicy.POLICY_REF, new NamespaceContextPolicy(_contextSelector));

            List<Activator> activators = ActivatorLoader.createActivators(
                    _appServiceDomain, components, _deployment.getActivationTypes());
            _deployment.init(_appServiceDomain, activators);
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
     * Get the application domain.
     * 
     * @return ServiceDomain
     */
    public ServiceDomain getDomain() {
        return _appServiceDomain;
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

    private QName getName(SwitchYardModel config) {
        if (config == null) {
            return null;
        }
        QName name = config.getQName();
        if (name == null && config.getComposite() != null) {
            name = config.getComposite().getQName();
        }
        return name;
    }
}
