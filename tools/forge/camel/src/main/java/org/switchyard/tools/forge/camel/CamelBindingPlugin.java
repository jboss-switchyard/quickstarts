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

import java.net.URI;

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
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;
import org.switchyard.tools.forge.common.CommonFacet;

/**
 * Forge plugin for Camel binding commands.
 */
@Alias("camel-binding")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, CommonFacet.class, CamelFacet.class})
@Topic("SOA")
@Help("Provides commands to manage Camel service bindings in SwitchYard.")
public class CamelBindingPlugin implements Plugin {

    @Inject
    private Project _project;
    
    /**
     * Bind a promoted service using the Camel binding.
     * @param serviceName name of the service to bind
     * @param configURI camel endpoint URI
     * @param out shell output
     */
    @Command(value = "bind-service", help = "Add a Camel binding to a service.")
    public void bindService(
            @Option(required = true,
                    name = "serviceName",
                    description = "The service name") 
            final String serviceName,
            @Option(required = true,
                    name = "configURI",
                    description = "The configuration URI") 
            final String configURI,
            final PipeOut out) {

        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeServiceModel service = switchYard.getCompositeService(serviceName);
        // Check to see if the service is public
        if (service == null) {
            out.println(out.renderColor(ShellColor.RED, "No public service named: " + serviceName));
            return;
        }
        
        V1CamelBindingModel binding = new V1CamelBindingModel();
        binding.setConfigURI(URI.create(configURI));
        service.addBinding(binding);
        switchYard.saveConfig();
        out.println("Added binding.camel to service " + serviceName);
    }
    

    /**
     * Bind a promoted reference using the Camel binding.
     * @param referenceName name of the reference to bind
     * @param configURI camel endpoint URI
     * @param out shell output
     */
    @Command(value = "bind-reference", help = "Add a Camel binding to a reference.")
    public void bindReference(
            @Option(required = true,
                    name = "referenceName",
                    description = "The reference name") 
            final String referenceName,
            @Option(required = true,
                    name = "configURI",
                    description = "The configuration URI") 
            final String configURI,
            final PipeOut out) {

        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeReferenceModel reference = switchYard.getCompositeReference(referenceName);
        // Check to see if the reference is public
        if (reference == null) {
            out.println(out.renderColor(ShellColor.RED, "No public reference named: " + referenceName));
            return;
        }
        
        V1CamelBindingModel binding = new V1CamelBindingModel();
        binding.setConfigURI(URI.create(configURI));
        
        reference.addBinding(binding);
        switchYard.saveConfig();
        out.println("Added binding.camel to reference " + referenceName);
    }
    
}
