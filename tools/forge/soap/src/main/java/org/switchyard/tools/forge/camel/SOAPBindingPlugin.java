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

package org.switchyard.tools.forge.camel;

import org.jboss.seam.forge.shell.ShellColor;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.Command;
import org.jboss.seam.forge.shell.plugins.Help;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.PipeOut;
import org.jboss.seam.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.shell.plugins.RequiresProject;
import org.jboss.seam.forge.shell.plugins.Topic;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.tools.forge.AbstractPlugin;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Forge commands related to SOAP bindings.
 */
@Alias("soap-binding")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, SOAPFacet.class})
@Topic("SOA")
@Help("Provides commands to manage SOAP service bindings in SwitchYard.")
public class SOAPBindingPlugin extends AbstractPlugin {
    
    /**
     * Add a SOAP binding to a SwitchYard service.
     * @param serviceName name of the serivce to bind
     * @param wsdlLocation location of the WSDL to configure the SOAP binding
     * @param port optional value for the endpoint port
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
                    name = "serverPort",
                    description = "Bind to this port for SOAP endpoints") 
            final Integer port,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = getProject().getFacet(SwitchYardFacet.class);
        CompositeServiceModel service = switchYard.getCompositeService(serviceName);
        // Check to see if the service is public
        if (service == null) {
            out.println(out.renderColor(ShellColor.RED, "No public service named: " + serviceName));
            return;
        }
        
        SOAPBindingModel binding = new SOAPBindingModel();
        binding.setWsdl(wsdlLocation);
        if (port != null) {
            binding.setServerPort(port);
        }
        service.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.soap to service " + serviceName);
    }
    
}
