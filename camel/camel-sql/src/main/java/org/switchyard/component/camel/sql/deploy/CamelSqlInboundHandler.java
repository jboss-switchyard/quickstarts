/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel.sql.deploy;

import javax.xml.namespace.QName;

import org.apache.camel.model.RouteDefinition;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.camel.common.SwitchYardRouteDefinition;
import org.switchyard.component.camel.common.handler.InboundHandler;
import org.switchyard.component.camel.common.handler.MessageComposerProcessor;
import org.switchyard.component.camel.common.handler.OperationSelectorProcessor;
import org.switchyard.component.camel.sql.model.CamelSqlBindingModel;
import org.switchyard.exception.SwitchYardException;
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
                throw new SwitchYardException("Period attribute is mandatory for SQL service bindings");
            }

            SwitchYardRouteDefinition definition = new SwitchYardRouteDefinition(serviceName);
            definition.routeId(getRouteId())
                .from(getBindingModel().getTimerURI(getRouteId()).toString())
                .to(getBindingModel().getComponentURI().toString())
                .setProperty(ExchangeCompletionEvent.GATEWAY_NAME).simple(getBindingModel().getName(), String.class)
                .process(new MessageComposerProcessor(getBindingModel()))
                .process(new OperationSelectorProcessor(serviceName, bindingModel));
            return addTransactionPolicy(definition);
        }
        return super.createRouteDefinition();
    }

}
