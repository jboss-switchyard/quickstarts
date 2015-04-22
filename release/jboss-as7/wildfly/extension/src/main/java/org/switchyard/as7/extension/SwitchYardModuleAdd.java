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
package org.switchyard.as7.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;
//import org.jboss.as.web.WebSubsystemServices;
import org.jboss.dmr.ModelNode;
import org.jboss.jca.core.spi.rar.ResourceAdapterRepository;
import org.jboss.logging.Logger;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.switchyard.as7.extension.deployment.SwitchYardModuleDependencyProcessor;
import org.switchyard.as7.extension.services.SwitchYardComponentService;
import org.switchyard.as7.extension.services.SwitchYardInjectorService;
import org.switchyard.deploy.Component;

/**
 * The SwitchYard subsystem's module add update handler.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class SwitchYardModuleAdd extends AbstractAddStepHandler {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    // TODO use ConnectorServices.RA_REPOSITORY_SERVICE instead once JBoss AS is updated to 7.1.1 or later
    //private static final ServiceName RA_REPOSITORY_SERVICE_NAME = ConnectorServices.RA_REPOSITORY_SERVICE;
    private static final ServiceName RA_REPOSITORY_SERVICE_NAME = ServiceName.JBOSS.append("rarepository");

    static final SwitchYardModuleAdd INSTANCE = new SwitchYardModuleAdd();

    /**
     * Global component names.
     */
    private static List<String> _componentNames = new ArrayList<String>();

    /**
     * Global priority.
     */
    public static int _priority = 0x4005;

    private SwitchYardModuleAdd() {

    }

    /**
     * Get the list of configured component names.
     * 
     * @return the list of components
     */
    public static List<String> getComponentNames() {
        return _componentNames;
    }

    @Override
    protected void populateModel(final ModelNode operation, final Resource resource) {
        final ModelNode model = resource.getModel();

        populateModel(operation, model);
    }

    @Override
    protected void populateModel(ModelNode operation, ModelNode subModel) {
        subModel.get(CommonAttributes.IMPLCLASS).set(operation.get(CommonAttributes.IMPLCLASS));
        if (operation.hasDefined(CommonAttributes.PROPERTIES)) {
            subModel.get(CommonAttributes.PROPERTIES).set(operation.get(CommonAttributes.PROPERTIES));
        }
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model,
                                  ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers) throws OperationFailedException {
        final String moduleId = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        _componentNames.add(moduleId);

        context.addStep(new AbstractDeploymentChainStep() {
            protected void execute(DeploymentProcessorTarget processorTarget) {
                processorTarget.addDeploymentProcessor(SwitchYardExtension.SUBSYSTEM_NAME, Phase.DEPENDENCIES, _priority++, new SwitchYardModuleDependencyProcessor(moduleId));
            }
        }, OperationContext.Stage.RUNTIME);

        final SwitchYardComponentService componentService = new SwitchYardComponentService(moduleId, model);
        final ServiceBuilder<Component> componentServiceBuilder = context.getServiceTarget().addService(SwitchYardComponentService.SERVICE_NAME.append(moduleId), componentService);
        componentServiceBuilder.addDependency(SwitchYardInjectorService.SERVICE_NAME, Map.class, componentService.getInjectedValues())
                .addDependency(RA_REPOSITORY_SERVICE_NAME, ResourceAdapterRepository.class, componentService.getResourceAdapterRepository());
        //componentServiceBuilder.addDependency(WebSubsystemServices.JBOSS_WEB);
        componentServiceBuilder.setInitialMode(Mode.ACTIVE);
        newControllers.add(componentServiceBuilder.install());
    }

}
