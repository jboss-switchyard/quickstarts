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
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.util.URISupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.switchyard.Exchange;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.component.camel.common.SwitchYardRouteDefinition;
import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.component.camel.common.transaction.TransactionManagerFactory;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.exception.SwitchYardException;

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
 */
public class InboundHandler extends BaseServiceHandler {

    /** operation selector ref. */
    public static final String OPERATION_SELECTOR_REF = "_operatoinSelector";

    private static final String TRANSACTED_REF = "transactionPolicy";
    private static TransactionManagerFactory TM_FACTORY = TransactionManagerFactory.getInstance();
    private final CamelBindingModel _camelBindingModel;
    private final SwitchYardCamelContext _camelContext;
    private QName _serviceName;

    /**
     * Sole constructor.
     * 
     * @param camelBindingModel The {@link CamelBindingModel}.
     * @param camelContext The camel context instance.
     * @param serviceName The target service name.
     */
    public InboundHandler(final CamelBindingModel camelBindingModel, final SwitchYardCamelContext camelContext, final QName serviceName) {
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
        final RouteDefinition route = new SwitchYardRouteDefinition(getServiceName().getNamespaceURI());
        final URI componentURI = getBindingModel().getComponentURI();

        route.routeId(getRouteId()).from(componentURI.toString());
        addTransactionPolicy(route, componentURI);

        return route.process(new OperationSelectorProcessor(getServiceName(), getBindingModel()))
            .to(CamelConstants.SWITCHYARD_COMPONENT_NAME +"://" + getServiceName().getLocalPart());
    }

    /**
     * Get route id for given binding.
     * 
     * @param serviceName Service name.
     * @return Camel route id.
     */
    protected String getRouteId() {
        return getServiceName().toString() + "-[" + getBindingModel().getComponentURI() + "]";
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
     */
    protected void addTransactionPolicy(final RouteDefinition route, final URI componentURI) {
        if (hasTransactionManager(componentURI.toString())) {
            final String tmName = getTransactionManagerName(componentURI);
            if (!isRegisteredInCamelRegistry(tmName) && isDefaultJtaTransactionName(tmName)) {
                final PlatformTransactionManager tm = TM_FACTORY.create();
                addToCamelRegistry(tm, tmName);
            }
            // Tell Camel the route is transacted
            route.transacted(TRANSACTED_REF);
        }
    }

    private boolean isDefaultJtaTransactionName(final String tmName) {
        return tmName.equals(TransactionManagerFactory.TM);
    }

    private boolean isRegisteredInCamelRegistry(final String tmName) {
        return _camelContext.getRegistry().lookup(tmName) != null;
    }

    private void addToCamelRegistry(final PlatformTransactionManager tm, final String tmName) {
        // Add the transaction manager
        _camelContext.getWritebleRegistry().put(tmName, tm);
        // Add a policy ref bean pointing to the transaction manager
        _camelContext.getWritebleRegistry().put(TRANSACTED_REF, new SpringTransactionPolicy(tm));
    }

    protected boolean hasTransactionManager(final String componentURI) {
        return componentURI.contains("transactionManager");
    }

    protected String getTransactionManagerName(final URI componentURI) {
        try {
            final Map<String, Object> parseParameters = URISupport.parseParameters(componentURI);
            String name = (String) parseParameters.get("transactionManager");
            if (name != null) {
                name = name.replace("#", "");
            }
            return name;
        } catch (URISyntaxException e) {
            throw new SwitchYardException(e);
        }
    }

    protected CamelBindingModel getBindingModel() {
        return _camelBindingModel;
    }

    /**
     * Will create the Camel route and add it to the camel context.
     */
    @Override
    public void start() {
        try {
            RouteDefinition route = _camelContext.getRouteDefinition(getRouteId());
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
    public void stop() {
        try {
            _camelContext.stopRoute(getRouteId());
            _camelContext.removeRoute(getRouteId());
        } catch (Exception ex) {
            throw new SwitchYardException("Failed to stop route for service " + _serviceName, ex);
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
