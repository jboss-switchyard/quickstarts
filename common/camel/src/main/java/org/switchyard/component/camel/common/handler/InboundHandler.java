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
package org.switchyard.component.camel.common.handler;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.camel.model.RouteDefinition;
import org.switchyard.Exchange;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardException;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.component.camel.common.CommonCamelLogger;
import org.switchyard.component.camel.common.CommonCamelMessages;
import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.component.camel.common.transaction.TransactionHelper;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * An ExchangeHandler that acts as a gateway/entrypoint for Camel Components.
 * 
 * This gives access to all component of Apache Camel and works by creating a
 * Camel route that looks something like this 
 * <pre>
 * from("CamelComponentURI").to("switchyard://serviceName"); 
 * </pre>
 * 
 * @author Daniel Bevenius
 * 
 * @param <T> Type of the binding.
 */
public class InboundHandler<T extends CamelBindingModel> extends BaseServiceHandler {

    private final T _camelBindingModel;
    private final SwitchYardCamelContext _camelContext;
    private final QName _serviceName;

    /**
     * Sole constructor.
     * 
     * @param camelBindingModel The {@link CamelBindingModel}.
     * @param camelContext The camel context instance.
     * @param serviceName The target service name.
     * @param domain the service domain.
     */
    public InboundHandler(final T camelBindingModel, final SwitchYardCamelContext camelContext, final QName serviceName, final ServiceDomain domain) {
        super(domain);
        _camelBindingModel = camelBindingModel;
        _camelContext = camelContext;
        _serviceName = serviceName;

        try {
            _camelContext.addRouteDefinition(createRouteDefinition());
        } catch (final Exception e) {
            throw new SwitchYardException(e);
        }
    }

    /**
     * Creates route definition applicable for actual camel binding model.
     * 
     * @return Route definition handling given binding.
     */
    protected RouteDefinition createRouteDefinition() {
        final RouteDefinition route = new RouteDefinition();

        route.routeId(getRouteId()).from(getComponentUri().toString());
        return addTransactionPolicy(route)
            .setProperty(ExchangeCompletionEvent.GATEWAY_NAME).simple(getBindingModel().getName(), String.class)
            .setProperty(CamelConstants.APPLICATION_NAMESPACE).constant(_serviceName.getNamespaceURI())
            .process(new MessageComposerProcessor(getBindingModel()))
            .process(new OperationSelectorProcessor(getServiceName(), getBindingModel()))
            .to(getSwitchyardEndpointUri());
    }

    /**
     * Get route id for given binding.
     * 
     * @return Camel route id.
     */
    public String getRouteId() {
        return getBindingModel().getClass().getSimpleName() + "/" + getServiceName().getLocalPart() + "@"
                + getBindingModel().getName() + "#" + getBindingModel().getComponentURI().hashCode();
    }

    protected QName getServiceName() {
        return _serviceName;
    }

    /**
     * Add transacted DSL element to route if necessary.
     * 
     * This method checks if component uri contains transaction manager reference
     * and puts 
     * 
     * @param route Route definition.
     * @param componentURI Component uri.
     * @return 
     */
    protected RouteDefinition addTransactionPolicy(final RouteDefinition route) {
        if (TransactionHelper.useTransactionManager(getComponentUri(), _camelContext)) {
            route.transacted(CamelConstants.TRANSACTED_REF);
        }
        
        return route;
    }

    protected URI getComponentUri() {
        return getBindingModel().getComponentURI();
    }

    protected String getSwitchyardEndpointUri() {
        return CamelConstants.SWITCHYARD_COMPONENT_NAME + "://" + getServiceName().getLocalPart();
    }

    protected T getBindingModel() {
        return _camelBindingModel;
    }
    
    protected SwitchYardCamelContext getSwitchYardCamelContext() {
        return _camelContext;
    }

    /**
     * Will create the Camel route and add it to the camel context.
     */
    @Override
    protected void doStart() {
        try {
            RouteDefinition route = _camelContext.getRouteDefinition(getRouteId());
            if (route == null) {
                // may have been removed if this was stopped
                route = createRouteDefinition();
                _camelContext.addRouteDefinition(route);
            }
            if (route.isStartable(_camelContext)) {
                _camelContext.startRoute(getRouteId());
            }
        } catch (Exception ex) {
            throw CommonCamelMessages.MESSAGES.failedToStartRouteForService(_serviceName.toString(), ex);
        }
    }

    /**
     * Suspends the route.
     */
    @Override
    protected void doStop() {
        try {
            _camelContext.stopRoute(getRouteId());
        } catch (Exception ex) {
            throw CommonCamelMessages.MESSAGES.failedToStopRouteForService(_serviceName.toString(), ex);
        }
        try {
            _camelContext.removeRoute(getRouteId());
        } catch (Exception ex) {
            CommonCamelLogger.ROOT_LOGGER.failedToRemoveRouteForService(_serviceName.toString(), ex);
        }
    }

    /**
     * This is a noop for this handler. This handler is only responsible for setting up
     * a route in camel and the {@link SwitchyardComponent} will take care of calling
     * the configured SwitchYard service.
     * 
     * @param switchYardExchange Message exchange.
     */
    @Override
    public void handleMessage(final Exchange switchYardExchange) {
    }

    @Override
    public void handleFault(final Exchange exchange) {
        System.out.println(exchange.getMessage().getContent());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_camelBindingModel == null) ? 0 : _camelBindingModel.getComponentURI().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final InboundHandler<?> other = (InboundHandler<?>) obj;
        if (_camelBindingModel == null) {
            if (other._camelBindingModel != null) {
                return false;
            }
        } else if (!_camelBindingModel.getComponentURI().equals(other._camelBindingModel.getComponentURI())) {
            return false;
        }
        return true;
    }

}
