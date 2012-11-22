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
package org.switchyard.as7.extension;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR; 
import static org.switchyard.as7.extension.CommonAttributes.IMPLCLASS;
import static org.switchyard.as7.extension.CommonAttributes.MODULE;
import static org.switchyard.as7.extension.CommonAttributes.PROPERTIES;
import static org.switchyard.as7.extension.CommonAttributes.SOCKET_BINDING;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.logging.Logger;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;

/**
 * The SwitchYard subsystem describe handler used by domain mode.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
final class SwitchYardSubsystemDescribe implements OperationStepHandler {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    static final SwitchYardSubsystemDescribe INSTANCE = new SwitchYardSubsystemDescribe();

    private SwitchYardSubsystemDescribe() {
        // forbidden inheritance
    }

    /* (non-Javadoc)
     * @see org.jboss.as.controller.OperationStepHandler#execute(org.jboss.as.controller.OperationContext, org.jboss.dmr.ModelNode)
     */
    @Override
    public void execute(final OperationContext context, final ModelNode operation) throws OperationFailedException {
        final ModelNode swydSusbsystemAdd = SwitchYardExtension.createAddSubsystemOperation();
        // Read the model
        final ModelNode swydSubsystem = context.readModel(PathAddress.EMPTY_ADDRESS);
        LOG.trace("Executing domain subsystem config " + operation);

        if (swydSubsystem.hasDefined(SOCKET_BINDING)) {
            swydSusbsystemAdd.get(SOCKET_BINDING).set(swydSubsystem.get(SOCKET_BINDING));
        }
        if (swydSubsystem.hasDefined(PROPERTIES)) {
            swydSusbsystemAdd.get(PROPERTIES).set(swydSubsystem.get(PROPERTIES));
        }
        context.getResult().add(swydSusbsystemAdd);

        for (Property property : swydSubsystem.get(MODULE).asPropertyList()) {
                ModelNode moduleAdd = new ModelNode();
                moduleAdd.get(OP).set(ADD);
                PathAddress addr = PathAddress.pathAddress(PathElement.pathElement(ModelDescriptionConstants.SUBSYSTEM, SwitchYardExtension.SUBSYSTEM_NAME), PathElement.pathElement(MODULE, property.getName()));
                moduleAdd.get(OP_ADDR).set(addr.toModelNode());
                if (property.getValue().hasDefined(IMPLCLASS)) {
                    moduleAdd.get(IMPLCLASS).set(property.getValue().get(IMPLCLASS));
                }
                if (property.getValue().hasDefined(PROPERTIES)) {
                    moduleAdd.get(PROPERTIES).set(property.getValue().get(PROPERTIES));
                }
                context.getResult().add(moduleAdd);
            }

        context.completeStep();
    }
}
