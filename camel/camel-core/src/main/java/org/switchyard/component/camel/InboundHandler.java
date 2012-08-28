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

import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.util.URISupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.switchyard.Exchange;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.camel.composer.CamelBindingData;
import org.switchyard.component.camel.config.model.CamelBindingModel;
import org.switchyard.component.camel.transaction.TransactionManagerFactory;
import org.switchyard.component.common.selector.OperationSelector;
import org.switchyard.component.common.selector.OperationSelectorFactory;
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
 *
 */
public class InboundHandler extends BaseServiceHandler {
    /** operation selector ref. */
    public static final String OPERATION_SELECTOR_REF = "_operatoinSelector";
    
    private static final String TRANSACTED_REF = "transactionPolicy";
    private static TransactionManagerFactory TM_FACTORY = TransactionManagerFactory.getInstance();
    private final CamelBindingModel _camelBindingModel;
    private final ModelCamelContext _camelContext;
    private RouteDefinition _routeDefinition;
    private QName _serviceName;

    /**
     * Sole constructor.
     * 
     * @param camelBindingModel The {@link CamelBindingModel}.
     * @param camelContext The {@link ModelCamelContext}.
     * @param serviceName The target service name.
     */
    public InboundHandler(final CamelBindingModel camelBindingModel, final ModelCamelContext camelContext, final QName serviceName) {
        _camelBindingModel = camelBindingModel;
        _camelContext = camelContext;
        _routeDefinition = createRouteDefinition(serviceName);
        _serviceName = serviceName;
        OperationSelector<CamelBindingData> selector = OperationSelectorFactory
                                                        .getOperationSelectorFactory(CamelBindingData.class)
                                                        .newOperationSelector(camelBindingModel.getOperationSelector());
        if (selector != null) {
            selector.setDefaultNamespace(Strings.trimToNull(serviceName.getNamespaceURI()));
            addToCamelRegistry(selector);
        }
        
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

    private void addToCamelRegistry(final OperationSelector<CamelBindingData> selector) {
        final SimpleRegistry simpleRegistry = new SimpleRegistry();
        simpleRegistry.put(_serviceName.getLocalPart() + OPERATION_SELECTOR_REF, selector);
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
        return new StringBuilder()
                    .append("switchyard://")
                    .append(serviceName.getLocalPart())
                    .toString();

    }

    /**
     * Will create the Camel route and add it to the {@link ModelCamelContext}.
     */
    @Override
    public void start() {
        try {
            if (_routeDefinition.getStatus(_camelContext).isStartable()) {
                _camelContext.startRoute(_routeDefinition.getId());
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
        final String routeId = _routeDefinition.getId();
        try {
            _camelContext.stopRoute(routeId);
            _camelContext.removeRoute(routeId);
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
