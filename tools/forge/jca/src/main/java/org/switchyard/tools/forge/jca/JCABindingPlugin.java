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

package org.switchyard.tools.forge.jca;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.Topic;
import org.switchyard.component.jca.config.model.ActivationSpecModel;
import org.switchyard.component.jca.config.model.ConnectionModel;
import org.switchyard.component.jca.config.model.EndpointModel;
import org.switchyard.component.jca.config.model.InboundConnectionModel;
import org.switchyard.component.jca.config.model.InboundInteractionModel;
import org.switchyard.component.jca.config.model.JCABindingModel;
import org.switchyard.component.jca.config.model.ListenerModel;
import org.switchyard.component.jca.config.model.OutboundConnectionModel;
import org.switchyard.component.jca.config.model.OutboundInteractionModel;
import org.switchyard.component.jca.config.model.ProcessorModel;
import org.switchyard.component.jca.config.model.ResourceAdapterModel;
import org.switchyard.component.jca.config.model.v1.V1ActivationSpecModel;
import org.switchyard.component.jca.config.model.v1.V1ConnectionModel;
import org.switchyard.component.jca.config.model.v1.V1EndpointModel;
import org.switchyard.component.jca.config.model.v1.V1InboundConnectionModel;
import org.switchyard.component.jca.config.model.v1.V1InboundInteractionModel;
import org.switchyard.component.jca.config.model.v1.V1JCABindingModel;
import org.switchyard.component.jca.config.model.v1.V1ListenerModel;
import org.switchyard.component.jca.config.model.v1.V1OutboundConnectionModel;
import org.switchyard.component.jca.config.model.v1.V1OutboundInteractionModel;
import org.switchyard.component.jca.config.model.v1.V1ProcessorModel;
import org.switchyard.component.jca.config.model.v1.V1ResourceAdapterModel;
import org.switchyard.component.jca.endpoint.JMSEndpoint;
import org.switchyard.component.jca.processor.JMSProcessor;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;
import org.switchyard.tools.forge.common.CommonFacet;

/**
 * Forge commands related to JCA bindings.
 */
@Alias("jca-binding")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, CommonFacet.class, JCAFacet.class})
@Topic("SOA")
@Help("Provides commands to manage JCA service bindings in SwitchYard.")
public class JCABindingPlugin implements Plugin {

    @Inject
    private Project _project;
    
    @Inject
    private Shell _shell;
 
    /**
     * Add a JCA binding to a SwitchYard service.
     * @param serviceName name of the reference to bind
     * @param resourceAdapter name of the ResourceAdapter archive
     * @param listener listener interface
     * @param endpoint fully qualified name of endpoint implementation class
     * @param transacted true if it should be transacted 
     * @param out shell output
     */
    @Command(value = "bind-service", help = "Add a JCA binding to a service.")
    public void bindService(
            @Option(required = true,
                    name = "serviceName",
                    description = "The service name") 
            final String serviceName,
            @Option(required = false,
                    name = "resourceAdapter",
                    defaultValue = "hornetq-ra.rar",
                    description = "Name of the ResourceAdapter archive")
            final String resourceAdapter,
            @Option(required = false,
                    name = "listener",
                    defaultValue = "javax.jms.Listener",
                    description = "Fully qualified name of the listener interface")
            final String listener,
            @Option(required = false,
                    name = "endpoint",
                    defaultValue = "org.switchyard.component.jca.endpoint.JMSEndpoint",
                    description = "Fully qualified name of the endpoint class")
            final String endpoint,
            @Option(required = false,
                    name = "transacted",
                    defaultValue = "true",
                    description = "Whether the binding should be transacted or not")
            final Boolean transacted,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeServiceModel service = switchYard.getCompositeService(serviceName);
        // Check to see if the service is public
        if (service == null) {
            out.println(out.renderColor(ShellColor.RED, "No public service named: " + serviceName));
            return;
        }

        JCABindingModel binding = new V1JCABindingModel();
        InboundConnectionModel connectionModel = new V1InboundConnectionModel();
        ResourceAdapterModel rar = new V1ResourceAdapterModel();
        rar.setName(resourceAdapter);
        connectionModel.setResourceAdapter(rar);
        if (endpoint.equals(JMSEndpoint.class.getName())) {
            ActivationSpecModel activationSpec = new V1ActivationSpecModel();
            String destinationType = _shell.promptCommon("destinationType", PromptType.ANY, "javax.jms.Queue");
            String destination = _shell.promptCommon("destination name", PromptType.ANY, "InboundQueue");
            activationSpec.setProperty("destinationType", destinationType);
            activationSpec.setProperty("destination", destination);
            connectionModel.setActivationSpec(activationSpec);
        }
        binding.setInboundConnection(connectionModel);
        
        InboundInteractionModel interactionModel = new V1InboundInteractionModel();
        ListenerModel listenerModel = new V1ListenerModel();
        listenerModel.setClassName(listener);
        interactionModel.setListener(listenerModel);
        EndpointModel endpointModel = new V1EndpointModel();
        endpointModel.setEndpointClassName(endpoint);
        interactionModel.setEndpoint(endpointModel);
        interactionModel.setTransacted(transacted);
        binding.setInboundInteraction(interactionModel);
        
        service.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.jca to service " + serviceName);
    }
    

    /**
     * Add a JCA binding to a SwitchYard reference.
     * @param referenceName name of the reference to bind
     * @param resourceAdapter name of the ResourceAdapter archive
     * @param connectionFactory ConnectionFactory JNDI name
     * @param processor fully qualified name of processor implementation class
     * @param out shell output
     */
    @Command(value = "bind-reference", help = "Add a SOAP binding to a reference.")
    public void bindReference(
            @Option(required = true,
                    name = "referenceName",
                    description = "The reference name") 
            final String referenceName,
            @Option(required = false,
                    name = "resourceAdapter",
                    defaultValue = "hornetq-ra.rar",
                    description = "Name of the ResourceAdapter archive")
            final String resourceAdapter,
            @Option(required = false,
                    name = "connectionFactory",
                    defaultValue = "java:/JmsXA",
                    description = "JNDI name of the ConnectionFactory") 
            final String connectionFactory,
            @Option(required = false,
                    name = "processor",
                    defaultValue = "org.switchyard.component.jca.processor.JMSProcessor",
                    description = "Fully qualified name of the outbound processor class") 
            final String processor,
            final PipeOut out) {

        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeReferenceModel reference = switchYard.getCompositeReference(referenceName);
        // Check to see if the service is public
        if (reference == null) {
            out.println(out.renderColor(ShellColor.RED, "No public reference named: " + referenceName));
            return;
        }

        JCABindingModel binding = new V1JCABindingModel();
        OutboundConnectionModel connectionModel = new V1OutboundConnectionModel();
        ResourceAdapterModel rarModel = new V1ResourceAdapterModel();
        rarModel.setName(resourceAdapter);
        connectionModel.setResourceAdapter(rarModel);
        ConnectionModel connection = new V1ConnectionModel();
        connection.setConnectionFactoryJNDIName(connectionFactory);
        connectionModel.setConnection(connection);
        binding.setOutboundConnection(connectionModel);
        
        OutboundInteractionModel interactionModel = new V1OutboundInteractionModel();
        ProcessorModel processorModel = new V1ProcessorModel();
        processorModel.setProcessorClassName(processor);
        if (processor.equals(JMSProcessor.class.getName())) {
            String destination = _shell.promptCommon("destination name", PromptType.ANY, "OutboundQueue");
            processorModel.setProperty("destination", destination);
        }
        interactionModel.setProcessor(processorModel);
        binding.setOutboundInteraction(interactionModel);
        
        reference.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.jca to reference " + referenceName);
    }
}
