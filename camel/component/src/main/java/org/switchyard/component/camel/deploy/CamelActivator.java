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
package org.switchyard.component.camel.deploy;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.switchyard.ServiceReference;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.common.property.PropertyResolver;
import org.switchyard.component.camel.ComponentNameComposer;
import org.switchyard.component.camel.RouteFactory;
import org.switchyard.component.camel.SwitchYardConsumer;
import org.switchyard.component.camel.SwitchYardEndpoint;
import org.switchyard.component.camel.SwitchYardPropertiesParser;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.component.camel.common.composer.CamelComposition;
import org.switchyard.component.camel.common.deploy.BaseBindingActivator;
import org.switchyard.component.camel.model.CamelComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.exception.SwitchYardException;

/**
 * Activates Camel bindings, references and implementations in SwitchYard. 
 * 
 * @author Daniel Bevenius
 */
public class CamelActivator extends BaseBindingActivator {

    /**
     * Creates a new activator for Camel endpoint types.
     * 
     * @param context Camel context to use.
     * @param types Activation types.
     */
    public CamelActivator(SwitchYardCamelContext context, String[] types) {
        super(context, types);
    }

    @Override
    public ServiceHandler activateService(QName serviceName, ComponentModel config) {
        ServiceHandler handler = null;

        // add switchyard property parser to camel PropertiesComponent
        PropertiesComponent propertiesComponent = getCamelContext().getComponent("properties", PropertiesComponent.class);
        PropertyResolver pr = config.getModelConfiguration().getPropertyResolver();
        propertiesComponent.setPropertiesParser(new SwitchYardPropertiesParser(pr));
        
        // process service
        for (ComponentServiceModel service : config.getServices()) {
            if (service.getQName().equals(serviceName)) {
                handler = handleImplementation(service, serviceName);
                break;
            }
        }

        return handler;
    }

    private ServiceHandler handleImplementation(final ComponentServiceModel config, final QName serviceName) {
        final CamelComponentImplementationModel ccim = 
                (CamelComponentImplementationModel)config.getComponent().getImplementation();
        try {
            final String endpointUri = ComponentNameComposer.composeComponentUri(serviceName);
            final RouteDefinition routeDef = getRouteDefinition(ccim);
            checkSwitchYardReferencedServiceExist(routeDef, ccim);
            addFromEndpointToRouteDefinition(routeDef, endpointUri);
            getCamelContext().addRouteDefinition(routeDef);
            final SwitchYardEndpoint endpoint = getCamelContext().getEndpoint(endpointUri, SwitchYardEndpoint.class);
            endpoint.setMessageComposer(CamelComposition.getMessageComposer());
            final SwitchYardConsumer consumer = endpoint.getConsumer();
            return consumer;
        } catch (final Exception e) {
            throw new SwitchYardException(e.getMessage(), e);
        }
    }

    private void checkSwitchYardReferencedServiceExist(
            final RouteDefinition routeDef, 
            final CamelComponentImplementationModel ccim) {
        
        final List<ProcessorDefinition<?>> outputs = routeDef.getOutputs();
        for (ProcessorDefinition<?> processorDef : outputs) {
            if (processorDef instanceof ToDefinition) {
                final ToDefinition to = (ToDefinition) processorDef;
                final URI componentUri = URI.create(to.getUri());
                if (componentUri.getScheme().equals(CamelConstants.SWITCHYARD_COMPONENT_NAME)) {
                    final String serviceName = componentUri.getHost();
                    final String namespace = ComponentNameComposer.getNamespaceFromURI(componentUri);
                    final QName refServiceName = new QName(namespace, serviceName);
                    if (!containsServiceRef(ccim.getComponent().getReferences(), serviceName)) {
                        throw new SwitchYardException("Could find the service reference for '" + serviceName + "'" 
                        + " which is referenced in " + to);
                    }
                    
                    final ServiceReference service = getServiceDomain().getServiceReference(refServiceName);
                    if (service == null) {
                        throw new SwitchYardException("Could find the service name '" + serviceName + "'" 
                        + " which is referenced in " + to);
                    }
                }
            }
        }
    }

    private boolean containsServiceRef(final List<ComponentReferenceModel> refs, final String serviceName) {
        for (ComponentReferenceModel refModel : refs) {
            if (refModel.getName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    private void addFromEndpointToRouteDefinition(final RouteDefinition rd, final String fromEndpointUri) throws Exception {
        final List<FromDefinition> inputs = rd.getInputs();

        // Make sure the route starts with a single switchyard:// endpoint
        if (inputs.size() == 0) {
            inputs.add(new FromDefinition(fromEndpointUri));
        } else if (inputs.size() == 1) {
            String routeURI = inputs.get(0).getUri();
            if (!fromEndpointUri.equals(routeURI)) {
                throw new SwitchYardException("Endpoint URI on route " + routeURI
                        + " does not match expected URI : " + fromEndpointUri);
            }
        } else {
            throw new SwitchYardException("A route can only have one 'from' endpoint");
        }

    }

    /**
     * There are two options for Camel implementation : Spring XML or Java DSL.
     * This method figures out which one were dealing with and returns the
     * corresponding RouteDefinition.
     */
    private RouteDefinition getRouteDefinition(CamelComponentImplementationModel model) {
        RouteDefinition routeDef = model.getRoute();
        if (routeDef == null && model.getJavaClass() != null) {
            routeDef = RouteFactory.createRoute(model.getJavaClass(), model.getComponent().getTargetNamespace());
        }
        return routeDef;
    }

}
