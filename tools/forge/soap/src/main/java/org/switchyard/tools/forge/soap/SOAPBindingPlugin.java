/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Forge commands related to SOAP bindings.
 */
@Alias("soap-binding")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, SOAPFacet.class})
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
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeServiceModel service = switchYard.getCompositeService(serviceName);
        // Check to see if the service is public
        if (service == null) {
            out.println(out.renderColor(ShellColor.RED, "No public service named: " + serviceName));
            return;
        }
        
        SOAPBindingModel binding = new SOAPBindingModel();
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
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeReferenceModel reference = switchYard.getCompositeReference(referenceName);
        // Check to see if the service is public
        if (reference == null) {
            out.println(out.renderColor(ShellColor.RED, "No public reference named: " + referenceName));
            return;
        }
        
        SOAPBindingModel binding = new SOAPBindingModel();
        binding.setWsdl(wsdlLocation);
        if (portName != null) {
            binding.setPort(new PortName(portName));
        }
        reference.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.soap to reference " + referenceName);
    }
}
