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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.util.URISupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.camel.config.model.CamelBindingModel;
import org.switchyard.component.camel.config.model.OperationSelector;
import org.switchyard.component.camel.transaction.TransactionManagerFactory;
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
    private static final String TRANSACTED_REF = "transactionPolicy";
    private static TransactionManagerFactory TM_FACTORY = TransactionManagerFactory.getInstance();
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
        final RouteDefinition route = new SwitchYardRouteDefinition(serviceName.getNamespaceURI());
        final String routeId = serviceName.toString() + "-[" +_camelBindingModel.getComponentURI() + "]";
        final URI componentURI = uriFromBindingModel();
        
        route.routeId(routeId).from(componentURI.toString());
        
        if (hasTransactionManager(componentURI.toString())) {
            final String tmName = getTransactionManagerName(componentURI);
            if (!isRegisteredInCamelRegistry(tmName) && isDefaultJtaTransactionName(tmName)) {
                final PlatformTransactionManager tm = TM_FACTORY.create();
                addToCamelRegistry(tm, tmName);
            }
            // Tell Camel the route is transacted
            route.transacted(TRANSACTED_REF).to(composeSwitchYardComponentName(serviceName));
        } else {
            route.to(composeSwitchYardComponentName(serviceName));
        }
        
        return route;
    }
    
    private boolean isDefaultJtaTransactionName(final String tmName) {
        return tmName.equals(TransactionManagerFactory.TM);
    }
    
    private boolean isRegisteredInCamelRegistry(final String tmName) {
        return _camelContext.getRegistry().lookup(tmName) != null;
    }

    private void addToCamelRegistry(final PlatformTransactionManager tm, final String tmName) {
        final SimpleRegistry simpleRegistry = new SimpleRegistry();
        // Add the transaction manager
        simpleRegistry.put(tmName, tm);
        // Add a policy ref bean pointing to the transaction manager
        simpleRegistry.put(TRANSACTED_REF, new SpringTransactionPolicy(tm));
        // Set the camel registry to delegate to our new simple registry
        final PropertyPlaceholderDelegateRegistry delegateReg = (PropertyPlaceholderDelegateRegistry) _camelContext.getRegistry();
        final CompositeRegistry registry = (CompositeRegistry) delegateReg.getRegistry();
        registry.addRegistry(simpleRegistry);
    }


    private boolean hasTransactionManager(final String componentURI) {
        return componentURI.contains("transactionManager");
    }
    
    String getTransactionManagerName(final URI componentURI) {
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
    
    private URI uriFromBindingModel() {
        return _camelBindingModel.getComponentURI();
    }

    private String composeSwitchYardComponentName(final QName serviceName) {
        final StringBuilder sb = new StringBuilder().append("switchyard://").append(serviceName.getLocalPart());
        final String operationName = operationName();
        if (operationName != null) {
            sb.append("?operationName=").append(operationName);
        }
        final String namespace = namespace(serviceName);
        return SwitchYardRouteDefinition.addNamespaceParameter(sb.toString(), namespace);
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
     * Suspends the route associated with the {@link ServiceReference}.
     *
     * @param serviceRef The {@link ServiceReference} of the target service.
     * @throws Exception If an error occurs while trying to suspend the route from the {@link CamelContext}.
     */
    public void stop(final ServiceReference serviceRef) throws Exception {
        final String routeId = _routeDefinition.getId();
        _camelContext.stopRoute(routeId);
        _camelContext.removeRoute(routeId);
    }

    private String namespace(QName serviceName) {
        String namespace = null;
        final OperationSelector os = _camelBindingModel.getOperationSelector();
        if (os != null) {
            namespace = Strings.trimToNull(_camelBindingModel.getOperationSelector().getNamespace());
        }
        if (namespace == null) {
            namespace = Strings.trimToNull(serviceName.getNamespaceURI());
        }
        return namespace;
    }

    private String operationName() {
        final OperationSelector os = _camelBindingModel.getOperationSelector();
        if (os != null) {
            return Strings.trimToNull(_camelBindingModel.getOperationSelector().getOperationName());
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
