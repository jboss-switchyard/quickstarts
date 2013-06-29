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
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.URL;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.switchyard.admin.Application;
import org.switchyard.admin.SwitchYard;
import org.switchyard.as7.extension.services.SwitchYardAdminService;
import org.switchyard.config.model.switchyard.ArtifactModel;

/**
 * SwitchYardSubsystemUsesArtifact
 * 
 * Operation returning the applications which depend on a given artifact.
 */
public final class SwitchYardSubsystemUsesArtifact implements OperationStepHandler {

    /**
     * The global instance for this operation.
     */
    public static final SwitchYardSubsystemUsesArtifact INSTANCE = new SwitchYardSubsystemUsesArtifact();

    private SwitchYardSubsystemUsesArtifact() {
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
                final ModelNode applications = context.getResult();
                final ServiceController<?> controller = context.getServiceRegistry(false).getRequiredService(
                        SwitchYardAdminService.SERVICE_NAME);
                
                String name = operation.hasDefined(NAME) ? operation.get(NAME).asString() : null;
                String url = operation.hasDefined(URL) ? operation.get(URL).asString() : null;
                
                SwitchYard switchYard = SwitchYard.class.cast(controller.getService().getValue());
                for (Application application : switchYard.getApplications()) {
                    if (application.getConfig().getArtifacts() == null) {
                        continue;
                    }
                    for (ArtifactModel artiact : application.getConfig().getArtifacts().getArtifacts()) {
                        // check for name match if a name filter was provided
                        if (name != null && !name.equals(artiact.getName())) {
                            continue;
                        }
                        // check for url match if a URL fiter was provided
                        if (url != null && !url.equals(artiact.getURL())) {
                            continue;
                        }
                        applications.add(application.getName().toString());
                    }
                }
                context.stepCompleted();
            }
        }, OperationContext.Stage.RUNTIME);
        context.stepCompleted();
    }
}
