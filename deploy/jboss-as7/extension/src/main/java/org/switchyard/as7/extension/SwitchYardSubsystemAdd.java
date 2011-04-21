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

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;

import org.jboss.as.controller.BasicOperationResult;
import org.jboss.as.controller.ModelAddOperationHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationResult;
import org.jboss.as.controller.ResultHandler;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.server.BootOperationContext;
import org.jboss.as.server.BootOperationHandler;
import org.jboss.as.server.deployment.Phase;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.switchyard.as7.extension.deployment.SwitchYardConfigDeploymentProcessor;
import org.switchyard.as7.extension.deployment.SwitchYardDependencyProcessor;
import org.switchyard.as7.extension.deployment.SwitchYardDeploymentProcessor;

/**
 * The SwitchYard subsystem add update handler.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public final class SwitchYardSubsystemAdd implements ModelAddOperationHandler, BootOperationHandler {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    static final SwitchYardSubsystemAdd INSTANCE = new SwitchYardSubsystemAdd();

    // Private to ensure a singleton.
    private SwitchYardSubsystemAdd() {
    }

    @Override
    public OperationResult execute(final OperationContext context, final ModelNode operation, final ResultHandler resultHandler) throws OperationFailedException {

        if (context instanceof BootOperationContext) {
            final BootOperationContext bootContext = (BootOperationContext) context;
            LOG.info("Activating SwitchYard Extension");
            int priority = 0x4000;
            bootContext.addDeploymentProcessor(Phase.PARSE, priority++, new SwitchYardConfigDeploymentProcessor());
            bootContext.addDeploymentProcessor(Phase.DEPENDENCIES, priority++, new SwitchYardDependencyProcessor());
            bootContext.addDeploymentProcessor(Phase.INSTALL, priority++, new SwitchYardDeploymentProcessor());
        }
        context.getSubModel().setEmptyObject();
        // Create the compensating operation
        final ModelNode compensatingOperation = Util.getResourceRemoveOperation(operation.require(OP_ADDR));
        resultHandler.handleResultComplete();
        return new BasicOperationResult(compensatingOperation);
    }

}
