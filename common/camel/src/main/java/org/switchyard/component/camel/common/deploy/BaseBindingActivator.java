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
package org.switchyard.component.camel.common.deploy;

import javax.xml.namespace.QName;

import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.composer.CamelComposition;
import org.switchyard.component.camel.common.handler.InboundHandler;
import org.switchyard.component.camel.common.handler.OutboundHandler;
import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.deploy.ServiceHandler;

/**
 * Activator for handling camel bindings on both, service and reference, sides.
 * 
 * @author Lukasz Dywicki
 */
public class BaseBindingActivator extends BaseCamelActivator {

    protected BaseBindingActivator(SwitchYardCamelContext context, String ... types) {
        super(context, types);
    }

    @Override
    public ServiceHandler activateBinding(QName serviceName, BindingModel config) {
        CamelBindingModel binding = (CamelBindingModel) config;
        binding.setEnvironment(getEnvironment());

        if (binding.isServiceBinding()) {
            return createInboundHandler(serviceName, binding);
        } else {
            return createOutboundHandler(binding);
        }
    }

    protected <T extends CamelBindingModel> InboundHandler<T> createInboundHandler(QName serviceName, T binding) {
        return new InboundHandler<T>(binding, getCamelContext(), serviceName, getServiceDomain());
    }

    protected <T extends CamelBindingModel> OutboundHandler createOutboundHandler(T binding) {
        return new OutboundHandler(binding, getCamelContext(), CamelComposition
                .getMessageComposer(binding), getServiceDomain());
    }

    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        // anything to do here?
    }

}
