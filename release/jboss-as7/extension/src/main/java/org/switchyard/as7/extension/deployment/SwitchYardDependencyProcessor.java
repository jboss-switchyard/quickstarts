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
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.filter.PathFilter;
import org.jboss.modules.filter.PathFilters;
import org.switchyard.as7.extension.SwitchYardDeploymentMarker;

/**
 * DU processor which adds dependencies for runtime modules needed for SwitchYard deployments.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardDependencyProcessor implements DeploymentUnitProcessor {

    private static final PathFilter META_INF_FILTER = PathFilters.isChildOf("META-INF");
    private static final ModuleIdentifier SWITCHYARD_ID = ModuleIdentifier.create("org.switchyard");
    private static final ModuleIdentifier SWITCHYARD_API_ID = ModuleIdentifier.create("org.switchyard.api");
    private static final ModuleIdentifier SWITCHYARD_COMMON_ID = ModuleIdentifier.create("org.switchyard.common");
    private static final ModuleIdentifier SWITCHYARD_COMMON_CAMEL_ID = ModuleIdentifier.create("org.switchyard.common.camel");
    private static final ModuleIdentifier SWITCHYARD_BUS_CAMEL = ModuleIdentifier.create("org.switchyard.bus.camel");
    private static final ModuleIdentifier SWITCHYARD_CONFIG_ID = ModuleIdentifier.create("org.switchyard.config");
    private static final ModuleIdentifier SWITCHYARD_RUNTIME_ID = ModuleIdentifier.create("org.switchyard.runtime");
    private static final ModuleIdentifier SWITCHYARD_TRANSFORM_ID = ModuleIdentifier.create("org.switchyard.transform");
    private static final ModuleIdentifier SWITCHYARD_VALIDATE_ID = ModuleIdentifier.create("org.switchyard.validate");
    private static final ModuleIdentifier DELTASPIKE_ID = ModuleIdentifier.create("org.apache.deltaspike.core-api");

    /**
     * Construct SwitchYard dependency processor.
     */
    public SwitchYardDependencyProcessor() {
    }

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#deploy(org.jboss.as.server.deployment.DeploymentPhaseContext)
     */
    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        final ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);
        if (!SwitchYardDeploymentMarker.isSwitchYardDeployment(deploymentUnit)) {
            return;
        }

        final ModuleLoader moduleLoader = Module.getBootModuleLoader();

        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, SWITCHYARD_ID, false, false, true, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, SWITCHYARD_API_ID, false, false, false, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, SWITCHYARD_COMMON_ID, false, false, false, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, DELTASPIKE_ID, false, false, false, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, SWITCHYARD_BUS_CAMEL, false, false, false, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, SWITCHYARD_COMMON_CAMEL_ID, false, false, false, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, SWITCHYARD_CONFIG_ID, false, false, false, false));
        ModuleDependency dep = new ModuleDependency(moduleLoader, SWITCHYARD_RUNTIME_ID, false, false, true, false);
        dep.addImportFilter(META_INF_FILTER, true);
        moduleSpecification.addSystemDependency(dep);
        dep = new ModuleDependency(moduleLoader, SWITCHYARD_TRANSFORM_ID, false, false, true, false);
        dep.addImportFilter(META_INF_FILTER, true);
        moduleSpecification.addSystemDependency(dep);
        dep = new ModuleDependency(moduleLoader, SWITCHYARD_VALIDATE_ID, false, false, true, false);
        dep.addImportFilter(META_INF_FILTER, true);
        moduleSpecification.addSystemDependency(dep);
    }

    
    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#undeploy(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public void undeploy(DeploymentUnit deploymentUnit) {

    }

}
