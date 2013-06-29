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

import org.jboss.as.clustering.infinispan.subsystem.CacheService;
import org.jboss.as.ee.component.EEModuleDescription;
import org.jboss.as.naming.context.NamespaceContextSelector;
import org.jboss.as.naming.deployment.JndiNamingDependencyProcessor;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.weld.WeldDeploymentMarker;
import org.jboss.as.weld.WeldStartService;
import org.jboss.as.weld.services.BeanManagerService;
import org.jboss.logging.Logger;
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
        
        switchyardServiceBuilder.addDependency(DependencyType.OPTIONAL, CacheService.getServiceName("cluster", null));

        switchyardServiceBuilder.setInitialMode(Mode.ACTIVE);
        switchyardServiceBuilder.install();
    }

    @Override
    public void undeploy(DeploymentUnit deploymentUnit) {
    }

}
