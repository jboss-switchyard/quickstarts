/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
