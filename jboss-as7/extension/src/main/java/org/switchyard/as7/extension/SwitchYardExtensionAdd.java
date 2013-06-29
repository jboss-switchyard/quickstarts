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
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.switchyard.as7.extension.deployment.SwitchYardModuleDependencyProcessor;

/**
 * The SwitchYard subsystem's extension add update handler.
 */
public final class SwitchYardExtensionAdd extends AbstractAddStepHandler {

    static final SwitchYardExtensionAdd INSTANCE = new SwitchYardExtensionAdd();

    /**
     * Global component names.
     */
    private static List<String> _extensionNames = new ArrayList<String>();

    private SwitchYardExtensionAdd() {

    }

    /**
     * Get the list of configured extension names.
     * 
     * @return the list of extensions
     */
    public static List<String> getExtensionNames() {
        return _extensionNames;
    }

    @Override
    protected void populateModel(final ModelNode operation, final Resource resource) {
        final ModelNode model = resource.getModel();

        populateModel(operation, model);
    }

    @Override
    protected void populateModel(ModelNode operation, ModelNode subModel) {
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model,
                                  ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers) throws OperationFailedException {
        final String moduleId = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        _extensionNames.add(moduleId);

        context.addStep(new AbstractDeploymentChainStep() {
            protected void execute(DeploymentProcessorTarget processorTarget) {
                processorTarget.addDeploymentProcessor(SwitchYardExtension.SUBSYSTEM_NAME, Phase.DEPENDENCIES, 0x4002, new SwitchYardModuleDependencyProcessor(moduleId));
            }
        }, OperationContext.Stage.RUNTIME);
    }

}
