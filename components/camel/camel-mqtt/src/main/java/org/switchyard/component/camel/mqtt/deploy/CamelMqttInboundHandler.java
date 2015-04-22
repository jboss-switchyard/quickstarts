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
package org.switchyard.component.camel.mqtt.deploy;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.RouteDefinition;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.common.type.Classes;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.component.camel.common.handler.InboundHandler;
import org.switchyard.component.camel.common.handler.MessageComposerProcessor;
import org.switchyard.component.camel.common.handler.OperationSelectorProcessor;
import org.switchyard.component.camel.mqtt.model.CamelMqttBindingModel;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * Inbound handler for MQTT binding. Set applicationContextClassLoader in CamelContext as a TCCL
 * as MQTT consumer runs with other class loader, causes issues. See https://issues.jboss.org/browse/SWITCHYARD-2220
 */
public class CamelMqttInboundHandler extends InboundHandler<CamelMqttBindingModel> {

    private static final String ORIGINAL_CLASS_LOADER = "org.switchyard.component.camel.mqtt.deploy.CamelMqttInboundHandler.originalClassLoader";

    /**
     * Sole constructor.
     * 
     * @param camelBindingModel The CamelBindingModel.
     * @param camelContext The camel context instance.
     * @param serviceName The target service name.
     * @param domain the service domain.
     */
    public CamelMqttInboundHandler(CamelMqttBindingModel camelBindingModel,
        SwitchYardCamelContext camelContext, QName serviceName, ServiceDomain domain) {
        super(camelBindingModel, camelContext, serviceName, domain);
    }

    @Override
    protected RouteDefinition createRouteDefinition() {
        final RouteDefinition route = new RouteDefinition();
        route.routeId(getRouteId()).from(getComponentUri().toString());
        addTransactionPolicy(route)
            .doTry()
                // SWITCHYARD-2220 - MQTT consumer needs TCCL to be set manually
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        ClassLoader appClassLoader = exchange.getContext().getApplicationContextClassLoader();
                        ClassLoader origCl = Classes.setTCCL(appClassLoader);
                        exchange.setProperty(ORIGINAL_CLASS_LOADER, origCl);
                    }
                })
                .setProperty(ExchangeCompletionEvent.GATEWAY_NAME).simple(getBindingModel().getName(), String.class)
                .setProperty(CamelConstants.APPLICATION_NAMESPACE).constant(getServiceName().getNamespaceURI())
                .process(new MessageComposerProcessor(getBindingModel()))
                .process(new OperationSelectorProcessor(getServiceName(), getBindingModel()))
                .to(getSwitchyardEndpointUri())
            .doFinally()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        ClassLoader origCl = exchange.getProperty(ORIGINAL_CLASS_LOADER, ClassLoader.class);
                        if (origCl != null) {
                            Classes.setTCCL(origCl);
                        }
                    }
                });
        return route;
    }

}
