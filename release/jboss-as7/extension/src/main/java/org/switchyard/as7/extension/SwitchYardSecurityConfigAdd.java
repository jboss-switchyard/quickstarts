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

import java.util.List;
import java.util.Map;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.registry.Resource;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.switchyard.as7.extension.services.SwitchYardInjectorService;
import org.switchyard.as7.extension.services.SwitchYardSecurityConfigService;
import org.switchyard.as7.extension.services.SwitchYardSecurityConfigService.SecurityConfig;
import org.switchyard.as7.extension.services.SwitchYardSystemSecurityService;
import org.switchyard.security.system.SystemSecurity;

/**
 * The SwitchYard subsystem's module add update handler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class SwitchYardSecurityConfigAdd extends AbstractAddStepHandler {

    static final SwitchYardSecurityConfigAdd INSTANCE = new SwitchYardSecurityConfigAdd();

    private SwitchYardSecurityConfigAdd() {}

    @Override
    protected void populateModel(final ModelNode operation, final Resource resource) {
        final ModelNode model = resource.getModel();
        populateModel(operation, model);
    }

    @Override
    protected void populateModel(ModelNode operation, ModelNode subModel) {
        if (operation.hasDefined(CommonAttributes.PROPERTIES)) {
            subModel.get(CommonAttributes.PROPERTIES).set(operation.get(CommonAttributes.PROPERTIES));
        }
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model,
                                  ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers) throws OperationFailedException {
        String moduleId = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        ServiceName serviceName = SwitchYardSecurityConfigService.SERVICE_NAME.append(moduleId);
        SwitchYardSecurityConfigService securityConfigService = new SwitchYardSecurityConfigService(moduleId, model);
        ServiceBuilder<SecurityConfig> securityConfigServiceBuilder = context.getServiceTarget().addService(serviceName, securityConfigService);
        securityConfigServiceBuilder.addDependency(SwitchYardSystemSecurityService.SERVICE_NAME, SystemSecurity.class, securityConfigService.getSystemSecurity());
        securityConfigServiceBuilder.addDependency(SwitchYardInjectorService.SERVICE_NAME, Map.class, securityConfigService.getInjectedValues());
        securityConfigServiceBuilder.setInitialMode(Mode.ACTIVE);
        newControllers.add(securityConfigServiceBuilder.install());
    }

}
