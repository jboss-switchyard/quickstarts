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

import static org.switchyard.as7.extension.CommonAttributes.MODULES;
import static org.switchyard.as7.extension.CommonAttributes.PROPERTIES;
import static org.switchyard.as7.extension.CommonAttributes.SOCKET_BINDING;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.network.SocketBinding;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;
import org.jboss.dmr.ModelNode;
import org.jboss.jca.core.spi.rar.ResourceAdapterRepository;
import org.jboss.logging.Logger;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.switchyard.as7.extension.deployment.SwitchYardCdiIntegrationProcessor;
import org.switchyard.as7.extension.deployment.SwitchYardConfigDeploymentProcessor;
import org.switchyard.as7.extension.deployment.SwitchYardConfigProcessor;
import org.switchyard.as7.extension.deployment.SwitchYardDependencyProcessor;
import org.switchyard.as7.extension.deployment.SwitchYardDeploymentProcessor;
import org.switchyard.as7.extension.services.SwitchYardAdminService;
import org.switchyard.as7.extension.services.SwitchYardComponentService;
import org.switchyard.as7.extension.services.SwitchYardInjectorService;
import org.switchyard.as7.extension.services.SwitchYardServiceDomainManagerService;
import org.switchyard.as7.extension.util.ServerUtil;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.ServiceDomainManager;

/**
 * The SwitchYard subsystem add update handler.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public final class SwitchYardSubsystemAdd extends AbstractBoottimeAddStepHandler {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    // TODO use ConnectorServices.RA_REPOSITORY_SERVICE instead once JBoss AS is updated to 7.1.1 or later
    //private static final ServiceName RA_REPOSITORY_SERVICE_NAME = ConnectorServices.RA_REPOSITORY_SERVICE;
    private static final ServiceName RA_REPOSITORY_SERVICE_NAME = ServiceName.JBOSS.append("rarepository");
            
    static final SwitchYardSubsystemAdd INSTANCE = new SwitchYardSubsystemAdd();

    // Private to ensure a singleton.
    private SwitchYardSubsystemAdd() {
    }

    @Override
    protected void populateModel(final ModelNode operation, final ModelNode submodel) throws OperationFailedException {
        if (operation.has(SOCKET_BINDING)) {
            submodel.get(SOCKET_BINDING).set(operation.get(SOCKET_BINDING));
        }
        if (operation.has(MODULES)) {
            submodel.get(MODULES).set(operation.get(MODULES));
        }
        if (operation.has(PROPERTIES)) {
            submodel.get(PROPERTIES).set(operation.get(PROPERTIES));
        }
    }

    @Override
    protected void performBoottime(OperationContext context, ModelNode operation, ModelNode model, ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers) {
        final List<ModuleIdentifier> modules = new ArrayList<ModuleIdentifier>();
        if (operation.has(CommonAttributes.MODULES)) {
            ModelNode opmodules = operation.get(CommonAttributes.MODULES);
            Set<String> keys = opmodules.keys();
            if (keys != null) {
                for (String current : keys) {
                    modules.add(ModuleIdentifier.fromString(current));
                }
            }
        }
        context.addStep(new AbstractDeploymentChainStep() {
            protected void execute(DeploymentProcessorTarget processorTarget) {
                int priority = 0x4000;
                processorTarget.addDeploymentProcessor(Phase.PARSE, priority++, new SwitchYardConfigDeploymentProcessor());
                processorTarget.addDeploymentProcessor(Phase.DEPENDENCIES, priority++, new SwitchYardDependencyProcessor(modules));
                processorTarget.addDeploymentProcessor(Phase.POST_MODULE, priority++, new SwitchYardCdiIntegrationProcessor());
                processorTarget.addDeploymentProcessor(Phase.POST_MODULE, priority++, new SwitchYardConfigProcessor());
                processorTarget.addDeploymentProcessor(Phase.INSTALL, priority++, new SwitchYardDeploymentProcessor());
            }
        }, OperationContext.Stage.RUNTIME);
        LOG.info("Activating SwitchYard Extension");

        ServerUtil.setRegistry(context.getServiceRegistry(false));
        final SwitchYardInjectorService injectorService = new SwitchYardInjectorService();
        final ServiceBuilder<Map<String, String>> injectorServiceBuilder = context.getServiceTarget().addService(SwitchYardInjectorService.SERVICE_NAME, injectorService);
        if (operation.has(SOCKET_BINDING)) {
            StringTokenizer sockets = new StringTokenizer(operation.get(SOCKET_BINDING).asString(), ",");
            while (sockets.hasMoreTokens()) {
                String socketName = sockets.nextToken();
                injectorServiceBuilder.addDependency(SocketBinding.JBOSS_BINDING_NAME.append(socketName), SocketBinding.class, injectorService.getSocketBinding(socketName));
            }
        }
        injectorServiceBuilder.setInitialMode(Mode.ACTIVE);
        newControllers.add(injectorServiceBuilder.install());

        final SwitchYardComponentService componentService = new SwitchYardComponentService(operation);
        final ServiceBuilder<List<Component>> componentServiceBuilder = context.getServiceTarget().addService(SwitchYardComponentService.SERVICE_NAME, componentService);
        componentServiceBuilder.addDependency(SwitchYardInjectorService.SERVICE_NAME, Map.class, componentService.getInjectedValues())
                .addDependency(RA_REPOSITORY_SERVICE_NAME, ResourceAdapterRepository.class, componentService.getResourceAdapterRepository());
        componentServiceBuilder.setInitialMode(Mode.ACTIVE);
        newControllers.add(componentServiceBuilder.install());

        // Add the AS7 Service for the ServiceDomainManager...
        newControllers.add(context.getServiceTarget().addService(SwitchYardServiceDomainManagerService.SERVICE_NAME, new SwitchYardServiceDomainManagerService()).install());

        final SwitchYardAdminService adminService = new SwitchYardAdminService();
        newControllers.add(context.getServiceTarget().addService(SwitchYardAdminService.SERVICE_NAME, adminService)
                .addDependency(SwitchYardComponentService.SERVICE_NAME, List.class, adminService.getComponents())
                .addDependency(SwitchYardInjectorService.SERVICE_NAME, Map.class, adminService.getSocketBindings())
                .addDependency(SwitchYardServiceDomainManagerService.SERVICE_NAME, ServiceDomainManager.class, adminService.getServiceDomainManager())
                .install());

    }

}
