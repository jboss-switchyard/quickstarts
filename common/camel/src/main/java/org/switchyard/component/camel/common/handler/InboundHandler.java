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
package org.switchyard.component.camel.common.handler;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.camel.model.RouteDefinition;
import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardException;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.component.camel.common.SwitchYardRouteDefinition;
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

    private static Logger _logger = Logger.getLogger(InboundHandler.class);
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
        final SwitchYardRouteDefinition route = new SwitchYardRouteDefinition(getServiceName());

        route.routeId(getRouteId()).from(getComponentUri().toString())
            .setProperty(ExchangeCompletionEvent.GATEWAY_NAME).simple(getBindingModel().getName(), String.class)
            .process(new MessageComposerProcessor(getBindingModel()))
            .process(new OperationSelectorProcessor(getServiceName(), getBindingModel()));
        return addTransactionPolicy(route);
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
    protected RouteDefinition addTransactionPolicy(final SwitchYardRouteDefinition route) {
        if (!TransactionHelper.useTransactionManager(getComponentUri(), _camelContext)) {
            // namespace will be added by SwitchYardRouteDefinition
            return route.to(getSwitchyardEndpointUri());
        }

        // Tell Camel the route is transacted
        route.transacted(CamelConstants.TRANSACTED_REF).to(getSwitchyardEndpointUri());
        // as we have 'transacted' element we need to process route outputs and
        // put namespace attribute for switchyard:// endpoint
        SwitchYardRouteDefinition.addNamespaceParameter(route, getServiceName().getNamespaceURI());
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
            throw new SwitchYardException("Failed to start route for service " + _serviceName, ex);
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
            throw new SwitchYardException("Failed to stop route for service " + _serviceName, ex);
        }
        try {
            _camelContext.removeRoute(getRouteId());
        } catch (Exception ex) {
            _logger.warn("Failed to remove route for service " + _serviceName, ex);
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
