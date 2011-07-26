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
package org.switchyard.tools.forge.plugin;

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
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeReferenceModel;
import org.switchyard.config.model.composite.v1.V1CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Project-level commands for SwitchYard applications.
 */
@Alias("switchyard")
@RequiresProject
@Topic("SOA")
@RequiresFacet(SwitchYardFacet.class)
@Help("Plugin for creating service-oriented applications with SwitchYard.")
public class SwitchYardPlugin implements Plugin {

    @Inject
    private Project _project;

    /**
     * List SwitchYard services available in the project.
     * @param out shell output
     */
    @Command(value = "show-config", help = "Show the current configuration state of the application.")
    public void listServices(final PipeOut out) {
        SwitchYardModel config = _project.getFacet(SwitchYardFacet.class).getMergedSwitchYardConfig();
        out.println();
        out.println("[Public]");
        // Print promoted service info
        for (CompositeServiceModel service : config.getComposite().getServices()) {
            out.print(out.renderColor(ShellColor.BOLD, "service: "));
            out.println(service.getName());
            out.print(out.renderColor(ShellColor.BOLD, "   interface: "));
            if (service.getInterface() != null) {
                out.println(service.getInterface().getInterface());
            } else {
                out.println(out.renderColor(ShellColor.YELLOW, "inherited"));
            }
            for (BindingModel binding : service.getBindings()) {
                out.print(out.renderColor(ShellColor.BOLD, "   binding: "));
                out.println(binding.getType());
            }
        }
        // Print promoted reference info
        for (CompositeReferenceModel reference : config.getComposite().getReferences()) {
            out.print(out.renderColor(ShellColor.BOLD, "reference: "));
            out.println(reference.getName());
            out.print(out.renderColor(ShellColor.BOLD, "   interface: "));
            if (reference.getInterface() != null) {
                out.println(reference.getInterface().getInterface());
            } else {
                out.println(out.renderColor(ShellColor.YELLOW, "inherited"));
            }
            for (BindingModel binding : reference.getBindings()) {
                out.print(out.renderColor(ShellColor.BOLD, "   binding: "));
                out.println(binding.getType());
            }
        }
        
        out.println();
        out.println("[Private]");
        for (ComponentModel component : config.getComposite().getComponents()) {
            out.print(out.renderColor(ShellColor.BOLD, "component: "));
            out.println(component.getName());
            for (ComponentServiceModel service : component.getServices()) {
                out.print(out.renderColor(ShellColor.BOLD, "   service: "));
                out.println(service.getName());
                out.print(out.renderColor(ShellColor.BOLD, "      interface: "));
                if (service.getInterface() != null) {
                    out.println(service.getInterface().getInterface());
                } else {
                    out.println(out.renderColor(ShellColor.RED, "unspecified"));
                }
            }
            for (ComponentReferenceModel reference : component.getReferences()) {
                out.print(out.renderColor(ShellColor.BOLD, "   reference: "));
                out.println(reference.getName());
                out.print(out.renderColor(ShellColor.BOLD, "      interface: "));
                if (reference.getInterface() != null) {
                    out.println(reference.getInterface().getInterface());
                } else {
                    out.println(out.renderColor(ShellColor.RED, "unspecified"));
                }
            }
        }
        out.println();
    }
    
    /**
     * Print SwitchYard version used in this application.
     * @param out shell output
     */
    @Command(value = "get-version", help = "Show the version of SwitchYard used by this application.")
    public void getVersion(final PipeOut out) {
        String version = _project.getFacet(SwitchYardFacet.class).getVersion();
        out.println("SwitchYard version " + version);
    }
    
    /**
     * Promote a component-level service to a composite-level service.
     * @param serviceName name of the service to promote
     * @param out shell output
     */
    @Command(value = "promote-service", help = "Promote a private service to public.")
    public void promoteService(
            @Option(required = true,
                     name = "serviceName",
                     description = "The service name") final String serviceName,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        
        // Check to see if the service is already promoted
        if (switchYard.getCompositeService(serviceName) != null) {
            out.println(out.renderColor(ShellColor.RED, "Service has already been promoted: " + serviceName));
            return;
        }
        // Make sure a component service exists
        if (switchYard.getComponentService(serviceName) == null) {
            out.println(out.renderColor(ShellColor.RED, "Component service not found: " + serviceName));
            return;
        }
        // Create the composite service
        V1CompositeServiceModel service = new V1CompositeServiceModel();
        service.setName(serviceName);
        service.setPromote(serviceName);
        switchYard.getSwitchYardConfig().getComposite().addService(service);
        
        // Save configuration changes
        switchYard.saveConfig();
        out.println("Promoted service " + serviceName);
    }
    

    /**
     * Promote a component-level reference to a composite-level reference.
     * @param referenceName name of the reference to promote
     * @param out shell output
     */
    @Command(value = "promote-reference", help = "Promote a private reference to public.")
    public void promoteReference(
            @Option(required = true,
                     name = "referenceName",
                     description = "The reference name") final String referenceName,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        
        // Check to see if the service is already promoted
        if (switchYard.getCompositeReference(referenceName) != null) {
            out.println(out.renderColor(ShellColor.RED, "Reference has already been promoted: " + referenceName));
            return;
        }
        // Make sure a component service exists
        ComponentReferenceModel component = switchYard.getComponentReference(referenceName);
        if (component == null) {
            out.println(out.renderColor(ShellColor.RED, "Component reference not found: " + referenceName));
            return;
        }
        // Create the composite service
        V1CompositeReferenceModel reference = new V1CompositeReferenceModel();
        reference.setName(referenceName);
        reference.setPromote(component.getComponent().getName() + "/" + referenceName);
        switchYard.getSwitchYardConfig().getComposite().addReference(reference);
        
        // Save configuration changes
        switchYard.saveConfig();
        out.println("Promoted reference " + referenceName);
    }
}
