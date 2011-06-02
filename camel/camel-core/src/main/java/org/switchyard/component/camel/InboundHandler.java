/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.component.camel.config.model.CamelBindingModel;
import org.switchyard.component.camel.config.model.OperationSelector;
import org.switchyard.exception.SwitchYardException;

/**
 * An {@link ExchangeHandler} that acts as a gateway/entrypoint for Camel Components.
 * 
 * This gives access to all component of Apache Camel and works by creating a
 * Camel route that looks something like this 
 * <pre>
 * from("CamelComponentURI").to("switchyard://serviceName?operationName=operationName"); 
 * </pre>
 * 
 * @author Daniel Bevenius
 *
 */
public class InboundHandler implements ExchangeHandler {
    
    private final CamelBindingModel _camelBindingModel;
    private final CamelContext _camelContext;
    private RouteDefinition _routeDefinition;

    /**
     * Sole constructor.
     * 
     * @param camelBindingModel The {@link CamelBindingModel}.
     * @param camelContext The {@link CamelContext}.
     * @param serviceName The target service name.
     */
    public InboundHandler(final CamelBindingModel camelBindingModel, final CamelContext camelContext, final QName serviceName) {
        _camelBindingModel = camelBindingModel;
        _camelContext = camelContext;
        _routeDefinition = createRouteDefinition(serviceName);
        
        try {
            _camelContext.addRouteDefinition(_routeDefinition);
        } catch (final Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
    private RouteDefinition createRouteDefinition(final QName serviceName) {
        final RouteDefinition rd = new RouteDefinition();
        final String routeId = serviceName.toString() + "-[" +_camelBindingModel.getComponentURI() + "]";
        return rd.routeId(routeId).from(uriFromBindingModel()).to(composeSwitchYardComponentName(serviceName));
    }
    
    private String uriFromBindingModel() {
        return _camelBindingModel.getComponentURI().toString();
    }

    private String composeSwitchYardComponentName(final QName serviceName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("switchyard://").append(serviceName.getLocalPart());
        
        final String operationName = operationName();
        if (operationName != null) {
            sb.append("?operationName=").append(operationName);
        }
        return sb.toString();
    }

    /**
     * Will create the Camel route and add it to the {@link CamelContext}.
     * 
     * @param serviceReference The target service reference.
     * @throws Exception If an error occurs while creating the route definition.
     */
    public void start(final ServiceReference serviceReference) throws Exception {
        if (_routeDefinition.getStatus(_camelContext).isStartable()) {
            _camelContext.startRoute(_routeDefinition);
        }
    }

    /**
     * Removes the route associated with the {@link ServiceReference}.
     *
     * @param serviceRef The {@link ServiceReference} of the target service.
     * @throws Exception If an error occurs while trying to removed the route from the {@link CamelContext}.
     */
    public void stop(final ServiceReference serviceRef) throws Exception {
        _camelContext.stopRoute(_routeDefinition);
    }

    private String operationName() {
        final OperationSelector os = _camelBindingModel.getOperationSelector();
        if (os != null) {
            return _camelBindingModel.getOperationSelector().getOperationName();
        }

        return null;
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
        
        final InboundHandler other = (InboundHandler) obj;
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
