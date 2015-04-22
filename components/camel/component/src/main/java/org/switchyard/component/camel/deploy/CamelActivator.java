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
import org.switchyard.SwitchYardException;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.common.property.PropertyResolver;
import org.switchyard.component.camel.CamelComponentMessages;
import org.switchyard.component.camel.switchyard.ComponentNameComposer;
import org.switchyard.component.camel.RouteFactory;
import org.switchyard.component.camel.switchyard.SwitchYardConsumer;
import org.switchyard.component.camel.switchyard.SwitchYardEndpoint;
import org.switchyard.component.camel.switchyard.SwitchYardPropertiesParser;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.component.camel.common.composer.CamelComposition;
import org.switchyard.component.camel.common.deploy.BaseCamelActivator;
import org.switchyard.component.camel.model.CamelComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.deploy.ComponentNames;
import org.switchyard.deploy.ServiceHandler;

/**
 * Activates Camel bindings, references and implementations in SwitchYard. 
 * 
 * @author Daniel Bevenius
 */
public class CamelActivator extends BaseCamelActivator {

    /**
     * Creates a new activator for Camel endpoint types.
     * 
     * @param context Camel context to use.
     * @param types Activation types.
     */
    public CamelActivator(SwitchYardCamelContext context, String ... types) {
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

    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        // nothing to do here ...
    }

    private ServiceHandler handleImplementation(final ComponentServiceModel config, final QName serviceName) {
        final CamelComponentImplementationModel ccim = 
                (CamelComponentImplementationModel)config.getComponent().getImplementation();
        try {
            final String endpointUri = ComponentNameComposer.composeComponentUri(serviceName);
            final List<RouteDefinition> routeDefinitions = getRouteDefinition(ccim);
            verifyRouteDefinitions(routeDefinitions, ccim);
            getCamelContext().addRouteDefinitions(routeDefinitions);
            final SwitchYardEndpoint endpoint = getCamelContext().getEndpoint(endpointUri, SwitchYardEndpoint.class);
            endpoint.setMessageComposer(CamelComposition.getMessageComposer());
            final SwitchYardConsumer consumer = endpoint.getConsumer();
            consumer.setComponentName(config.getComponent().getQName());
            consumer.setNamespace(serviceName.getNamespaceURI());
            return consumer;
        } catch (final Exception e) {
            throw new SwitchYardException(e.getMessage(), e);
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

    private void verifyRouteDefinitions(List<RouteDefinition> routeDefinitions, CamelComponentImplementationModel ccim) throws Exception {
        // service name & namespace
        // TODO what happens when we have multiple services?
        String serviceName = ccim.getComponent().getServices().get(0).getName();
        String compositeNs = ccim.getComponent().getComposite().getTargetNamespace();

        // number of switchyard:// consumers/from statements
        int serviceConsumer = 0;
        for (RouteDefinition routeDefinition : routeDefinitions) {
            if (routeDefinition.getInputs().isEmpty()) {
                throw CamelComponentMessages.MESSAGES.mustHaveAtLeastOneInput();
            }
            for (FromDefinition fromDefinition : routeDefinition.getInputs()) {
                URI from = URI.create(fromDefinition.getUri());
                if (from.getScheme().equals(CamelConstants.SWITCHYARD_COMPONENT_NAME)) {
                    if (serviceConsumer > 0) {
                        throw CamelComponentMessages.MESSAGES.onlyOneSwitchYardInputPerImpl();
                    }
                    String authority = from.getAuthority();

                    if (!serviceName.equals(authority)) {
                        throw CamelComponentMessages.MESSAGES.implementationConsumerDoesNotMatchService(serviceName);
                    }
                    serviceConsumer++;
                }
            }

            List<ProcessorDefinition<?>> outputs = routeDefinition.getOutputs();
            for (ProcessorDefinition<?> processorDefinition : outputs) {
                if (processorDefinition instanceof ToDefinition) {
                    ToDefinition to = (ToDefinition) processorDefinition;
                    final URI componentUri = URI.create(to.getUri());
                    if (componentUri.getScheme().equals(CamelConstants.SWITCHYARD_COMPONENT_NAME)) {
                        final String referenceName = componentUri.getAuthority();
                        final QName refServiceName = new QName(compositeNs, referenceName);
                        if (!containsServiceRef(ccim.getComponent().getReferences(), referenceName)) {
                            throw CamelComponentMessages.MESSAGES.couldNotFindServiceReference(referenceName, to.toString());
                        }
                        
                        QName qualifiedRefName = ComponentNames.qualify(
                                ccim.getComponent().getQName(), refServiceName);
                        final ServiceReference service = getServiceDomain().getServiceReference(qualifiedRefName);
                        if (service == null) {
                            throw CamelComponentMessages.MESSAGES.couldNotFindServiceName(qualifiedRefName.toString(), to.toString());
                        }
                    }
                }
            }
        }
        if (serviceConsumer != 1) {
            throw CamelComponentMessages.MESSAGES.cannotCreateComponentImpl();
        }
    }


    /**
     * There are two options for Camel implementation : Spring XML or Java DSL.
     * This method figures out which one were dealing with and returns the
     * corresponding RouteDefinition.
     */
    private List<RouteDefinition> getRouteDefinition(CamelComponentImplementationModel model) {
        List<RouteDefinition> routes = RouteFactory.getRoutes(model);
        return routes;
    }

}
