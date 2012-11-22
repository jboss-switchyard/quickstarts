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

import static org.switchyard.as7.extension.SwitchYardModelConstants.SERVICE_NAME;

import javax.xml.namespace.QName;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.switchyard.admin.Application;
import org.switchyard.admin.Service;
import org.switchyard.admin.SwitchYard;
import org.switchyard.as7.extension.services.SwitchYardAdminService;

/**
 * SwitchYardSubsystemShowMetrics
 * 
 * Operation returning metrics for services deployed on the SwitchYard subsystem.
 */
public final class SwitchYardSubsystemShowMetrics implements OperationStepHandler {

    /**
     * The global instance for this operation.
     */
    public static final SwitchYardSubsystemShowMetrics INSTANCE = new SwitchYardSubsystemShowMetrics();

    private SwitchYardSubsystemShowMetrics() {
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
                if (operation.hasDefined(SERVICE_NAME)) {
                    final String serviceName = operation.get(SERVICE_NAME).asString();
                    final QName serviceQName = QName.valueOf(serviceName);
                    for (Application application : switchYard.getApplications()) {
                        for (Service service : application.getServices()) {
                            if ("*".equals(serviceName) || serviceQName.equals(service.getName())) {
                                context.getResult().add(ModelNodeCreationUtil.createServiceMetricsNode(service));
                            }
                        }
                    }
                } else {
                    context.getResult().add(
                            ModelNodeCreationUtil.addMetricsToNode(
                                    new ModelNode(), switchYard.getMessageMetrics()));
                }
                
                context.completeStep();
            }
        }, OperationContext.Stage.RUNTIME);
        context.completeStep();
    }
    
}
