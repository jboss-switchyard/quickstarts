/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.as7.extension.admin;

import static org.switchyard.as7.extension.SwitchYardModelConstants.APPLICATION_NAME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.ENABLED;
import static org.switchyard.as7.extension.SwitchYardModelConstants.MAX_REQUESTS;
import static org.switchyard.as7.extension.SwitchYardModelConstants.SERVICE_NAME;
import static org.switchyard.as7.extension.SwitchYardModelConstants.THROTTLING;

import javax.xml.namespace.QName;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.switchyard.admin.Application;
import org.switchyard.admin.Service;
import org.switchyard.admin.SwitchYard;
import org.switchyard.admin.Throttling;
import org.switchyard.as7.extension.services.SwitchYardAdminService;

/**
 * SwitchYardSubsystemStartGateway
 * 
 * Operation for starting a gateway.
 */
public final class SwitchYardSubsystemUpdateThrottling implements OperationStepHandler {

    /**
     * The global instance for this operation.
     */
    public static final SwitchYardSubsystemUpdateThrottling INSTANCE = new SwitchYardSubsystemUpdateThrottling();

    private SwitchYardSubsystemUpdateThrottling() {
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
                final Application application = switchYard.getApplication(QName.valueOf(operation.get(APPLICATION_NAME)
                        .asString()));
                final QName serviceQName = QName.valueOf(operation.get(SERVICE_NAME).asString());
                final Service service = application.getService(serviceQName);
                try {
                    final Throttling throttling = service.getThrottling();
                    final ModelNode throttlingNode = operation.get(THROTTLING);
                    if (throttlingNode != null) {
                        final ModelNode enabled = throttlingNode.get(ENABLED);
                        final ModelNode maxRequests = throttlingNode.get(MAX_REQUESTS);
                        throttling.update(enabled == null ? null : enabled.asBoolean(), maxRequests == null ? null
                                : maxRequests.asInt());
                    }
                    context.stepCompleted();
                } catch (Throwable e) {
                    throw new OperationFailedException(new ModelNode().set("Error updating throttling: "
                            + e.getMessage()));
                }
            }
        }, OperationContext.Stage.RUNTIME);
        context.stepCompleted();
    }

}
