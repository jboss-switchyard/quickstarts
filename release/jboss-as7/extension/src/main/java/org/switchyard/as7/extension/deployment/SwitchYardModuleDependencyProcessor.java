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

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.as.weld.WeldDeploymentMarker;
import org.jboss.metadata.ear.spec.EarMetaData;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.filter.PathFilter;
import org.jboss.modules.filter.PathFilters;
import org.switchyard.as7.extension.SwitchYardDeploymentMarker;

/**
 * DU processor which adds a module dependency for modules needed for SwitchYard deployments.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class SwitchYardModuleDependencyProcessor implements DeploymentUnitProcessor {

    private static final PathFilter META_INF_FILTER = PathFilters.isChildOf("META-INF");
    private static final ModuleIdentifier SWITCHYARD_BEAN_ID = ModuleIdentifier.create("org.switchyard.component.bean");

    private String _moduleId;

    /**
     * Construct SwitchYard module dependency processor.
     *
     * @param moduleId The module identifier
     */
    public SwitchYardModuleDependencyProcessor(String moduleId) {
        _moduleId = moduleId;
    }

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#deploy(org.jboss.as.server.deployment.DeploymentPhaseContext)
     */
    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);
        if (!SwitchYardDeploymentMarker.isSwitchYardDeployment(deploymentUnit)) {
            return;
        }

        ModuleLoader moduleLoader = Module.getBootModuleLoader();
        ModuleDependency dep = new ModuleDependency(moduleLoader, ModuleIdentifier.fromString(_moduleId), false, false, true, false);
        dep.addImportFilter(META_INF_FILTER, true);
        moduleSpecification.addSystemDependency(dep);

        if (WeldDeploymentMarker.isPartOfWeldDeployment(deploymentUnit) && (deploymentUnit.getParent() != null)) {
            final EarMetaData earConfig = deploymentUnit.getParent().getAttachment(org.jboss.as.ee.structure.Attachments.EAR_METADATA);
            if (earConfig != null) {
                final ModuleIdentifier beanComponent = deploymentUnit.getParent().getAttachment(SwitchYardMetaData.BEAN_COMPONENT_ATTACHMENT_KEY);
                if (beanComponent == null) {
                    moduleLoader = Module.getBootModuleLoader();
                    moduleSpecification = deploymentUnit.getParent().getAttachment(Attachments.MODULE_SPECIFICATION);
                    dep = new ModuleDependency(moduleLoader, SWITCHYARD_BEAN_ID, false, false, true, false);
                    dep.addImportFilter(META_INF_FILTER, true);
                    moduleSpecification.addSystemDependency(dep);
                    deploymentUnit.getParent().putAttachment(SwitchYardMetaData.BEAN_COMPONENT_ATTACHMENT_KEY, SWITCHYARD_BEAN_ID);
                }
            }
        }
    }

    
    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#undeploy(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public void undeploy(DeploymentUnit deploymentUnit) {
    }

}
