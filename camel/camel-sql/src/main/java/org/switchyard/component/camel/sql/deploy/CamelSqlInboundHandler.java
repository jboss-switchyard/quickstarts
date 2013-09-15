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
package org.switchyard.component.camel.sql.deploy;

import javax.xml.namespace.QName;

import org.apache.camel.model.RouteDefinition;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.component.camel.common.handler.InboundHandler;
import org.switchyard.component.camel.common.handler.MessageComposerProcessor;
import org.switchyard.component.camel.common.handler.OperationSelectorProcessor;
import org.switchyard.component.camel.sql.model.CamelSqlBindingModel;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * Inbound handler for SQL binding. Creates additional route elements for service bindings.
 */
public class CamelSqlInboundHandler extends InboundHandler<CamelSqlBindingModel> {

    /**
     * Sole constructor.
     * 
     * @param camelBindingModel The CamelBindingModel.
     * @param camelContext The camel context instance.
     * @param serviceName The target service name.
     * @param domain the service domain.
     */
    public CamelSqlInboundHandler(CamelSqlBindingModel camelBindingModel,
        SwitchYardCamelContext camelContext, QName serviceName, ServiceDomain domain) {
        super(camelBindingModel, camelContext, serviceName, domain);
    }

    @Override
    protected RouteDefinition createRouteDefinition() {
        CamelSqlBindingModel bindingModel = getBindingModel();
        QName serviceName = getServiceName();

        String period = bindingModel.getPeriod();
        if (bindingModel.isServiceBinding()) {
            if (Strings.trimToNull(period) == null) {
                throw SQLCamelComponentMessages.MESSAGES.periodAttributeMandatory();
            }

            RouteDefinition definition = new RouteDefinition();
            definition.routeId(getRouteId())
                .from(getBindingModel().getTimerURI(getRouteId()).toString())
                .to(getBindingModel().getComponentURI().toString())
                .setProperty(ExchangeCompletionEvent.GATEWAY_NAME).simple(getBindingModel().getName(), String.class)
                .setProperty(CamelConstants.APPLICATION_NAMESPACE).constant(serviceName.getNamespaceURI())
                .process(new MessageComposerProcessor(getBindingModel()))
                .process(new OperationSelectorProcessor(serviceName, bindingModel));
            return addTransactionPolicy(definition);
        }
        return super.createRouteDefinition();
    }

}
