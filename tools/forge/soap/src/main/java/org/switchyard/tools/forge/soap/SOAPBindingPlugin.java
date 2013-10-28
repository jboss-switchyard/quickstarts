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

package org.switchyard.tools.forge.soap;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
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
import org.switchyard.common.net.SocketAddr;
import org.switchyard.component.soap.PortName;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.config.model.SOAPNamespace;
import org.switchyard.component.soap.config.model.v1.V1SOAPBindingModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.composite.v1.V1InterfaceModel;
import org.switchyard.tools.forge.common.CommonFacet;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Forge commands related to SOAP bindings.
 */
@Alias("soap-binding")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, CommonFacet.class, SOAPFacet.class})
@Topic("SOA")
@Help("Provides commands to manage SOAP service bindings in SwitchYard.")
public class SOAPBindingPlugin implements Plugin {

    @Inject
    private Project _project;
    
    /**
     * Add a SOAP binding to a SwitchYard service.
     * @param serviceName name of the reference to bind
     * @param wsdlLocation location of the WSDL to configure the SOAP binding
     * @param socketAddr optional value for the ip+port
     * @param portType optional value for interface portType
     * @param out shell output
     */
    @Command(value = "bind-service", help = "Add a SOAP binding to a service.")
    public void bindService(
            @Option(required = true,
                    name = "serviceName",
                    description = "The service name") 
            final String serviceName,
            @Option(required = true,
                    name = "wsdl",
                    description = "URL or package-local path to the endpoint WSDL") 
            final String wsdlLocation,
            @Option(required = false,
                    name = "socketAddr",
                    description = "Bind to this ip+port for SOAP endpoints") 
            final String socketAddr,
            @Option(required = false,
                    name = "portType",
                    description = "portType to use for interface definition") 
            final String portType,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeServiceModel service = switchYard.getCompositeService(serviceName);
        // Check to see if the service is public
        if (service == null) {
            out.println(out.renderColor(ShellColor.RED, "No public service named: " + serviceName));
            return;
        }
        
        if (portType != null) {
            InterfaceModel intf = new V1InterfaceModel(InterfaceModel.WSDL);
            intf.setInterface(wsdlLocation + "#wsdl.porttype(" + portType + ")");
            service.setInterface(intf);
        }
        
        SOAPBindingModel binding = new V1SOAPBindingModel(SOAPNamespace.DEFAULT.uri());
        binding.setWsdl(wsdlLocation);
        if (socketAddr != null) {
            binding.setSocketAddr(new SocketAddr(socketAddr));
        }
        service.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.soap to service " + serviceName);
    }
    

    /**
     * Add a SOAP binding to a SwitchYard reference.
     * @param referenceName name of the reference to bind
     * @param wsdlLocation location of the WSDL to configure the SOAP binding
     * @param portName optional value for the endpoint port
     * @param portType optional value for interface portType
     * @param out shell output
     */
    @Command(value = "bind-reference", help = "Add a SOAP binding to a reference.")
    public void bindReference(
            @Option(required = true,
                    name = "referenceName",
                    description = "The reference name") 
            final String referenceName,
            @Option(required = true,
                    name = "wsdl",
                    description = "URL or package-local path to the endpoint WSDL") 
            final String wsdlLocation,
            @Option(required = false,
                    name = "portName",
                    description = "Port name in WSDL") 
            final String portName,
            @Option(required = false,
                    name = "portType",
                    description = "portType to use for interface definition") 
            final String portType,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeReferenceModel reference = switchYard.getCompositeReference(referenceName);
        // Check to see if the service is public
        if (reference == null) {
            out.println(out.renderColor(ShellColor.RED, "No public reference named: " + referenceName));
            return;
        }

        if (portType != null) {
            InterfaceModel intf = new V1InterfaceModel(InterfaceModel.WSDL);
            intf.setInterface(wsdlLocation + "#wsdl.porttype(" + portType + ")");
            reference.setInterface(intf);
        }
        
        SOAPBindingModel binding = new V1SOAPBindingModel(SOAPNamespace.DEFAULT.uri());
        binding.setWsdl(wsdlLocation);
        if (portName != null) {
            binding.setPort(new PortName(portName));
        }
        reference.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.soap to reference " + referenceName);
    }
}
