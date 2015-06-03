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
package org.switchyard.as7.extension.admin;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NAME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.APPLICATION_NAME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.SERVICE_NAME;

import javax.xml.namespace.QName;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.Reference;
import org.switchyard.admin.Service;
import org.switchyard.admin.SwitchYard;
import org.switchyard.as7.extension.services.SwitchYardAdminService;

/**
 * SwitchYardSubsystemStartGateway
 * 
 * Operation for starting a gateway.
 */
public final class SwitchYardSubsystemStartGateway implements OperationStepHandler {

    /**
     * The global instance for this operation.
     */
    public static final SwitchYardSubsystemStartGateway INSTANCE = new SwitchYardSubsystemStartGateway();

    private SwitchYardSubsystemStartGateway() {
        // forbidden inheritance
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jboss.as.controller.OperationStepHandler#execute(org.jboss.as.controller
     * .OperationContext, org.jboss.dmr.ModelNode)
     */
    @Override
    public void execute(final OperationContext context, final ModelNode operation) throws OperationFailedException {

        context.addStep(new OperationStepHandler() {
            @Override
            public void execute(final OperationContext context, final ModelNode operation)
                    throws OperationFailedException {
                final ServiceController<?> controller = context.getServiceRegistry(false).getRequiredService(
                        SwitchYardAdminService.SERVICE_NAME);
                SwitchYard switchYard = SwitchYard.class.cast(controller.getService().getValue());
                final Binding binding;
                final String bindingName = operation.get(NAME).asString();
                final Application application = switchYard.getApplication(QName.valueOf(operation.get(APPLICATION_NAME)
                        .asString()));
                if (application == null) {
                    binding = null;
                } else {
                    final QName serviceQName = QName.valueOf(operation.get(SERVICE_NAME).asString());
                    final Service service = application.getService(serviceQName);
                    if (service == null) {
                        final Reference reference = application.getReference(serviceQName);
                        if (reference == null) {
                            binding = null;
                        } else {
                            binding = reference.getGateway(bindingName);
                        }
                    } else {
                        binding = service.getGateway(bindingName);
                    }
                }
                if (binding != null) {
                    try {
                        binding.start();
                        context.stepCompleted();
                    } catch (Throwable e) {
                        throw new OperationFailedException(new ModelNode().set("Error starting gateway: "
                                + e.getMessage()));
                    }
                    return;
                }
                throw new OperationFailedException(new ModelNode().set("Unknown gateway."));
            }
        }, OperationContext.Stage.RUNTIME);
        context.stepCompleted();
    }

}
