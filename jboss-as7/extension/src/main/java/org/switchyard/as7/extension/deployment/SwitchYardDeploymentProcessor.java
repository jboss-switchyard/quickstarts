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

import java.util.HashSet;
import java.util.Set;

import org.jboss.as.clustering.infinispan.subsystem.CacheService;
import org.jboss.as.connector.util.ConnectorServices;
import org.jboss.as.ee.component.EEModuleDescription;
import org.jboss.as.naming.context.NamespaceContextSelector;
import org.jboss.as.naming.deployment.JndiNamingDependencyProcessor;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.weld.WeldDeploymentMarker;
import org.jboss.as.weld.WeldStartService;
import org.jboss.as.weld.services.BeanManagerService;
import org.jboss.logging.Logger;
import org.jboss.metadata.ear.spec.EarMetaData;
import org.jboss.modules.Module;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceBuilder.DependencyType;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.value.ImmediateValue;
import org.switchyard.as7.extension.SwitchYardDeploymentMarker;
import org.switchyard.as7.extension.SwitchYardModuleAdd;
import org.switchyard.as7.extension.services.SwitchYardComponentService;
import org.switchyard.as7.extension.services.SwitchYardService;
import org.switchyard.as7.extension.services.SwitchYardServiceDomainManagerService;
import org.switchyard.component.jca.config.model.InboundConnectionModel;
import org.switchyard.component.jca.config.model.JCABindingModel;
import org.switchyard.component.jca.config.model.OutboundConnectionModel;
import org.switchyard.component.jca.config.model.ResourceAdapterModel;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.ServiceDomainManager;

/**
 * Deployment processor that installs the SwitchYard service and all other dependent services.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardDeploymentProcessor implements DeploymentUnitProcessor {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    /**
     * Construct SwitchYard deployment processor with a list of component modules.
     */
    public SwitchYardDeploymentProcessor() {
    }

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        if (!SwitchYardDeploymentMarker.isSwitchYardDeployment(deploymentUnit)) {
            return;
        }
        final DeploymentUnit parent = deploymentUnit.getParent();
        Boolean initializeInOrder = false;
        if (parent != null) {
            final EarMetaData earConfig = deploymentUnit.getParent().getAttachment(org.jboss.as.ee.structure.Attachments.EAR_METADATA);
            if (earConfig != null) {
                initializeInOrder = earConfig.getInitializeInOrder();
            }
        }
        doDeploy(phaseContext, deploymentUnit, initializeInOrder);
    }

    private void doDeploy(DeploymentPhaseContext phaseContext, final DeploymentUnit deploymentUnit, final Boolean initializeInOrder) {
        LOG.info("Deploying SwitchYard application '" + deploymentUnit.getName() + "'");

        ServiceDomainManager domainManager =
                (ServiceDomainManager) phaseContext.getServiceRegistry().getRequiredService(SwitchYardServiceDomainManagerService.SERVICE_NAME).getService().getValue();

        final ServiceTarget serviceTarget = phaseContext.getServiceTarget();
        SwitchYardMetaData metaData = deploymentUnit.getAttachment(SwitchYardMetaData.ATTACHMENT_KEY);
        SwitchYardDeployment deployment = new SwitchYardDeployment(deploymentUnit, metaData.getSwitchYardModel(), domainManager);
        SwitchYardService container = new SwitchYardService(deployment);
        final ServiceName switchyardServiceName = deploymentUnit.getServiceName().append(SwitchYardService.SERVICE_NAME);
        final ServiceBuilder<SwitchYardDeployment> switchyardServiceBuilder = serviceTarget.addService(switchyardServiceName, container);
        for (String componentName : SwitchYardModuleAdd.getComponentNames()) {
            switchyardServiceBuilder.addDependency(SwitchYardComponentService.SERVICE_NAME.append(componentName), Component.class, container.getComponent());
        }
        // ensure naming context is fully initialized before we start
        switchyardServiceBuilder.addDependency(JndiNamingDependencyProcessor.serviceName(deploymentUnit));

        final EEModuleDescription moduleDescription = deploymentUnit.getAttachment(org.jboss.as.ee.component.Attachments.EE_MODULE_DESCRIPTION);
        if (moduleDescription != null) {
            container.getNamespaceSelector().setValue(new ImmediateValue<NamespaceContextSelector>(moduleDescription.getNamespaceContextSelector()));
        }

        // Only add a dependency on the Weld BeanManager if the deployment has beans (i.e. Weld Metadata)...
        if (WeldDeploymentMarker.isPartOfWeldDeployment(deploymentUnit)) {
            final ServiceName beanManagerServiceName = deploymentUnit.getServiceName().append(BeanManagerService.NAME);
            switchyardServiceBuilder.addDependency(beanManagerServiceName);
            if (deploymentUnit.getParent() == null) {
                final ServiceName weldStartServiceName = deploymentUnit.getServiceName().append(WeldStartService.SERVICE_NAME);
                switchyardServiceBuilder.addDependency(weldStartServiceName);
            } else {
                final ServiceName weldStartServiceName = deploymentUnit.getParent().getServiceName().append(WeldStartService.SERVICE_NAME);
                switchyardServiceBuilder.addDependency(weldStartServiceName);
            }
        }
        
        // Collect all the resource adapters referenced from SwitchYard configuration
        Set<String> resourceAdapters = new HashSet<String>();
        ClassLoader origCl = Thread.currentThread().getContextClassLoader();
        try {
            final Module module = deploymentUnit.getAttachment(Attachments.MODULE);
            Thread.currentThread().setContextClassLoader(module.getClassLoader());
            CompositeModel composite = metaData.getSwitchYardModel().getComposite();
            for (CompositeServiceModel service : composite.getServices()) {
                for (BindingModel binding : service.getBindings()) {
                    if (binding instanceof JCABindingModel) {
                        JCABindingModel jcabinding = JCABindingModel.class.cast(binding);
                        InboundConnectionModel ic = jcabinding.getInboundConnection();
                        if (ic != null) {
                            ResourceAdapterModel ra = ic.getResourceAdapter();
                            if (ra != null && ra.getName() != null && !ra.getName().isEmpty()) {
                                resourceAdapters.add(ra.getName());
                            }
                        }
                    }
                }
            }
            for (CompositeReferenceModel reference : composite.getReferences()) {
                for (BindingModel binding : reference.getBindings()) {
                    if (binding instanceof JCABindingModel) {
                        JCABindingModel jcabinding = JCABindingModel.class.cast(binding);
                        OutboundConnectionModel oc = jcabinding.getOutboundConnection();
                        if (oc != null) {
                            ResourceAdapterModel ra = oc.getResourceAdapter();
                            if (ra != null && ra.getName() != null && !ra.getName().isEmpty()) {
                                resourceAdapters.add(ra.getName());
                            }
                        }
                    }
                }
            }
        } finally {
                Thread.currentThread().setContextClassLoader(origCl);
        }
        for (String raName : resourceAdapters) {
            switchyardServiceBuilder.addDependency(ConnectorServices.RESOURCE_ADAPTER_SERVICE_PREFIX
                                                                    .append(stripDotRarSuffix(raName)));
        }

        // Add dependency in the order defined in ear config
        if (initializeInOrder) {
            final DeploymentUnit parent = deploymentUnit.getParent();
            ServiceName previousServiceName = parent.getAttachment(SwitchYardMetaData.SERVICENAME_ATTACHMENT_KEY);
            if (previousServiceName != null) {
                switchyardServiceBuilder.addDependency(previousServiceName);
            }
            parent.putAttachment(SwitchYardMetaData.SERVICENAME_ATTACHMENT_KEY, switchyardServiceName);
        }

        switchyardServiceBuilder.addDependency(DependencyType.OPTIONAL, CacheService.getServiceName("cluster", null));

        switchyardServiceBuilder.setInitialMode(Mode.ACTIVE);
        switchyardServiceBuilder.install();
    }

    private String stripDotRarSuffix(final String raName) {
        if (raName == null) {
            return null;
        }
        if (raName.endsWith(".rar")) {
            return raName.substring(0, raName.indexOf(".rar"));
        }
        return raName;
    }

    @Override
    public void undeploy(DeploymentUnit deploymentUnit) {
    }

}
