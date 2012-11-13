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

import org.apache.camel.CamelContext;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.InboundHandler;
import org.switchyard.component.camel.OutboundHandler;
import org.switchyard.component.camel.RouteFactory;
import org.switchyard.component.camel.SwitchYardConsumer;
import org.switchyard.component.camel.SwitchyardEndpoint;
import org.switchyard.component.camel.composer.CamelBindingData;
import org.switchyard.component.camel.composer.CamelComposition;
import org.switchyard.component.camel.config.model.CamelBindingModel;
import org.switchyard.component.camel.config.model.CamelComponentImplementationModel;
import org.switchyard.component.camel.config.model.amqp.v1.V1CamelAmqpBindingModel;
import org.switchyard.component.camel.config.model.atom.v1.V1CamelAtomBindingModel;
import org.switchyard.component.camel.config.model.direct.v1.V1CamelDirectBindingModel;
import org.switchyard.component.camel.config.model.file.v1.V1CamelFileBindingModel;
import org.switchyard.component.camel.config.model.ftp.v1.V1CamelFtpBindingModel;
import org.switchyard.component.camel.config.model.ftps.v1.V1CamelFtpsBindingModel;
import org.switchyard.component.camel.config.model.jms.v1.V1CamelJmsBindingModel;
import org.switchyard.component.camel.config.model.jpa.v1.V1CamelJpaBindingModel;
import org.switchyard.component.camel.config.model.mail.v1.V1CamelMailBindingModel;
import org.switchyard.component.camel.config.model.mock.v1.V1CamelMockBindingModel;
import org.switchyard.component.camel.config.model.netty.v1.V1CamelNettyTcpBindingModel;
import org.switchyard.component.camel.config.model.netty.v1.V1CamelNettyUdpBindingModel;
import org.switchyard.component.camel.config.model.quartz.v1.V1CamelQuartzBindingModel;
import org.switchyard.component.camel.config.model.seda.v1.V1CamelSedaBindingModel;
import org.switchyard.component.camel.config.model.sftp.v1.V1CamelSftpBindingModel;
import org.switchyard.component.camel.config.model.sql.v1.V1CamelSqlBindingModel;
import org.switchyard.component.camel.config.model.timer.v1.V1CamelTimerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.exception.SwitchYardException;

/**
 * Activates Camel bindings, references and implementations in SwitchYard. 
 * 
 * @author Daniel Bevenius
 */
public class CamelActivator extends BaseActivator {

    /**
     * Property added to each Camel Context so that code initialized inside 
     * Camel can access the SY service domain.
     */
    public static final String SERVICE_DOMAIN = "org.switchyard.camel.serviceDomain";
    
    /**
     * Camel activation types.
     */
    public static final String[] CAMEL_TYPES = new String[] {
        V1CamelBindingModel.CAMEL, 
        V1CamelAtomBindingModel.ATOM,
        V1CamelDirectBindingModel.DIRECT,
        V1CamelFileBindingModel.FILE,
        V1CamelFtpBindingModel.FTP,
        V1CamelFtpsBindingModel.FTPS,
        V1CamelMockBindingModel.MOCK,
        V1CamelNettyTcpBindingModel.NETTY_TCP,
        V1CamelNettyUdpBindingModel.NETTY_UDP,
        V1CamelSedaBindingModel.SEDA,
        V1CamelSftpBindingModel.SFTP,
        V1CamelTimerBindingModel.TIMER,
        V1CamelJmsBindingModel.JMS,
        V1CamelQuartzBindingModel.QUARTZ,
        V1CamelSqlBindingModel.SQL,
        V1CamelMailBindingModel.MAIL,
        V1CamelJpaBindingModel.JPA,
        V1CamelAmqpBindingModel.AMQP
    };

    private SwitchYardCamelContext _camelContext;

    private Configuration _environment;

    /**
     * Creates a new activator for Camel endpoint types.
     * 
     * @param context Camel context to use.
     */
    public CamelActivator(SwitchYardCamelContext context) {
        super(CAMEL_TYPES);
        _camelContext = context;
    }

    @Override
    public void setServiceDomain(ServiceDomain serviceDomain) {
        super.setServiceDomain(serviceDomain);
        _camelContext.getWritebleRegistry().put(SERVICE_DOMAIN, serviceDomain);
    }

    @Override
    public ServiceHandler activateBinding(QName serviceName, BindingModel config) {
        CamelBindingModel binding = (CamelBindingModel)config;
        binding.setEnvironment(_environment);

        if (binding.isServiceBinding()) {
            return new InboundHandler(binding, _camelContext, serviceName);
        } else {
            return createOutboundHandler(binding, binding.getReference().getQName());
        }
    }
    
    @Override
    public ServiceHandler activateService(QName serviceName, ComponentModel config) {
        ServiceHandler handler = null;

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
    public void deactivateBinding(QName name, ServiceHandler handler) {
        // anything to do here?
    }

    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        // anything to do here?
    }

    private ServiceHandler handleImplementation(final ComponentServiceModel config, final QName serviceName) {
        
        final CamelComponentImplementationModel ccim = 
                (CamelComponentImplementationModel)config.getComponent().getImplementation();
        try {
            final String endpointUri = ComponentNameComposer.composeComponentUri(serviceName);
            final RouteDefinition routeDef = getRouteDefinition(ccim);
            checkSwitchYardReferencedServiceExist(routeDef, ccim);
            addFromEndpointToRouteDefinition(routeDef, endpointUri);
            _camelContext.addRouteDefinition(routeDef);
            final SwitchyardEndpoint endpoint = (SwitchyardEndpoint) _camelContext.getEndpoint(endpointUri);
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
                if (componentUri.getScheme().equals(ComponentNameComposer.SWITCHYARD_COMPONENT_NAME)) {
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

    private ServiceHandler createOutboundHandler(final CamelBindingModel binding, final QName name) {
        final String endpointUri = binding.getComponentURI().toString();
        final MessageComposer<CamelBindingData> messageComposer = CamelComposition.getMessageComposer(binding);
        return new OutboundHandler(endpointUri, _camelContext, messageComposer);
    }

    /**
     * Gets the {@link CamelContext} used by this Activator.
     * 
     * @return CamelContext the {@link CamelContext} used by this Activator.
     */
    public CamelContext getCamelContext() {
        return _camelContext;
    }

    /**
     * Set the Environment configuration for the activator.
     * @param config The global environment configuration.
     */
    public void setEnvironment(Configuration config) {
        _environment = config;
    }

}
