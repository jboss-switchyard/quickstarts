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

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.RouteDefinition;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.ServiceReference;
import org.switchyard.camel.component.SwitchyardEndpoint;
import org.switchyard.component.camel.config.model.CamelComponentImplementationModel;
import org.switchyard.component.camel.deploy.ComponentNameComposer;

/**
 * An ImplementationHandler is a SwitchYard ExchangeHandler that is used
 * for 'implementation.camel'.
 * </p>
 * 
 * A 'implmentation.camel' configuration specifies an interface for the component and 
 * this class is the implmentation. It takes care of creating a service for the interface
 * which sole purpose is to trigger the Camel route. It does this by creating a 'from' route 
 * that triggers the Camel route. This allows SwitchYard clients to use the service interface 
 * to invoke the Camel route.
 * </p> 
 * 
 * The format of the internal 'from' route looks like this:
 * <pre>
 *  switchyard://ServiceName 
 * </pre>
 * 
 * @author Daniel Bevenius
 *
 */
public class ImplementationHandler implements ExchangeHandler
{
    private final CamelComponentImplementationModel _configModel;
    private final CamelContext _camelContext;
    private DefaultConsumer _consumer;
    
    /**
     * 
     * @param serviceName The name of the SwitchYard service.
     * @param context The CamelContext to be used by this instance.
     * @param configModel The {@link CamelComponentImplementationModel} which must have a route definition configured.
     */
    public ImplementationHandler(final CamelContext context, final CamelComponentImplementationModel configModel) {
        _configModel = configModel;
        _camelContext = context;
    }
    
    /**
     * Gets the Camel route definition from the underlying configuration model and
     * adds a 'from' route which is used internally by SwitchYard to trigger processing
     * of the Camel route.
     * The format of this route will be
     * <pre>
     * from("switchyard://serviceName")
     * </pre>
     * 
     * @param serviceReference The reference to the serivce that is to be started.
     * @throws Exception if a route definition could not be created.
     */
    public void start(final ServiceReference serviceReference) throws Exception {
        final String endpointUri = ComponentNameComposer.composeComponenUri(serviceReference.getName());
        final RouteDefinition routeDef = addFromEndpointToRouteDefinition(_configModel.getRoute(), endpointUri);
        _camelContext.addRouteDefinition(routeDef);
        _consumer = getConsumerForEndpoint(endpointUri);
    }
    
    private RouteDefinition addFromEndpointToRouteDefinition(final RouteDefinition rd, final String fromEndpointUri) throws Exception {
        final List<FromDefinition> inputs = rd.getInputs();
        if (inputs.isEmpty() == false) {
            throw new RuntimeException("A Route must not define any 'from' endpoints as the 'from' endpoint will be created by SwithYard");
        }
        inputs.add(new FromDefinition(fromEndpointUri));
        return rd;
    }
    
    private DefaultConsumer getConsumerForEndpoint(final String endpointUri) {
        final SwitchyardEndpoint endpoint = (SwitchyardEndpoint) _camelContext.getEndpoint(endpointUri);
        return endpoint.getConsumer();
    }

    @Override
    public void handleMessage(final Exchange switchyardExchange) throws HandlerException {
        final org.apache.camel.Exchange camelExchange = createCamelExchange(switchyardExchange);
        invokeCamelProcessor(camelExchange);
            
        if (isInOut(switchyardExchange)) {
            sendResponse(camelExchange, switchyardExchange);
        }
    }
    
    /**
     * This method used the DefaultConsumer of the target Camel endpoint to invoke the 
     * camel processors.
     * 
     * @param camelExchange The Camel {@link org.apache.camel.Exchange} that is to be passed to the processors.
     * @throws Exception If an internal processing error occurs.
     */
    private void invokeCamelProcessor(final org.apache.camel.Exchange camelExchange) throws HandlerException {
        try {
	        _consumer.getProcessor().process(camelExchange);
        } catch (final Exception e) {
           throw new HandlerException(e); 
        }
    }
    
    private org.apache.camel.Exchange createCamelExchange(final Exchange switchyardExchange) {
        org.apache.camel.Exchange camelExchange = isInOut(switchyardExchange) ? 
                _consumer.getEndpoint().createExchange(org.apache.camel.ExchangePattern.InOut):
                _consumer.getEndpoint().createExchange(org.apache.camel.ExchangePattern.InOnly);
        
        camelExchange.getIn().setBody(switchyardExchange.getMessage().getContent());
        return camelExchange;
        
    }

    private void sendResponse(org.apache.camel.Exchange camelExchange, final Exchange switchyardExchange) {
        Object payload = camelExchange.getOut().getBody();
        if (payload == null) {
            payload = camelExchange.getIn().getBody();
        }
        switchyardExchange.getMessage().setContent(payload);
        switchyardExchange.send(switchyardExchange.getMessage());
    }

    private boolean isInOut(final Exchange exchange) {
        return exchange.getContract().getServiceOperation().getExchangePattern() == ExchangePattern.IN_OUT;
    }
    
    @Override
    public void handleFault(final Exchange exchange) {
        System.out.println("Handle fault");
        
    }
    
}
