/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
import static org.switchyard.as7.extension.SwitchYardModelConstants.REFERENCE_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.switchyard.admin.Application;
import org.switchyard.admin.Reference;
import org.switchyard.admin.SwitchYard;
import org.switchyard.as7.extension.services.SwitchYardAdminService;

/**
 * SwitchYardSubsystemReadReference
 * 
 * Operation returning details of the references deployed on the SwitchYard
 * subsystem.
 */
public final class SwitchYardSubsystemReadReference implements OperationStepHandler {

    /**
     * The global instance for this operation.
     */
    public static final SwitchYardSubsystemReadReference INSTANCE = new SwitchYardSubsystemReadReference();

    private SwitchYardSubsystemReadReference() {
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

                List<Reference> references = Collections.emptyList();
                SwitchYard switchYard = SwitchYard.class.cast(controller.getService().getValue());
                if (operation.hasDefined(APPLICATION_NAME)) {
                    final QName applicationName = QName.valueOf(operation.get(APPLICATION_NAME).asString());
                    final Application application = switchYard.getApplication(applicationName);
                    if (application != null) {
                        if (operation.hasDefined(REFERENCE_NAME)) {
                            Reference reference = findReference(QName.valueOf(operation.get(REFERENCE_NAME).asString()),
                                    application);
                            if (reference != null) {
                                references = Collections.singletonList(reference);
                            }
                        } else {
                            references = application.getReferences();
                        }
                    }
                } else if (operation.hasDefined(REFERENCE_NAME)) {
                    final QName referenceName = QName.valueOf(operation.get(REFERENCE_NAME).asString());
                    references = new ArrayList<Reference>();
                    for (Application application : switchYard.getApplications()) {
                        Reference reference = findReference(referenceName, application);
                        if (reference != null) {
                            references.add(reference);
                        }
                    }
                } else {
                    references = switchYard.getReferences();
                }

                addReferenceNodes(context.getResult(), references);

                context.stepCompleted();
            }
        }, OperationContext.Stage.RUNTIME);
        context.stepCompleted();
    }

    private void addReferenceNodes(ModelNode result, List<Reference> references) {
        for (Reference reference : references) {
            result.add(ModelNodeCreationUtil.createReferenceNode(reference));
        }
    }

    /**
     * TODO: should move this to Application
     */
    private Reference findReference(QName referenceName, Application application) {
        for (Reference reference : application.getReferences()) {
            if (referenceName.equals(reference.getName())) {
                return reference;
            }
        }
        return null;
    }
}
