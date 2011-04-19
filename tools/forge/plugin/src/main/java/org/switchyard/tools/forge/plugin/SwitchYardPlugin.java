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

import javax.xml.namespace.QName;

import org.jboss.seam.forge.shell.ShellColor;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.Command;
import org.jboss.seam.forge.shell.plugins.Help;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.PipeOut;
import org.jboss.seam.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.shell.plugins.RequiresProject;
import org.jboss.seam.forge.shell.plugins.Topic;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.tools.forge.AbstractPlugin;

/**
 * Project-level commands for SwitchYard applications.
 */
@Alias("switchyard")
@RequiresProject
@Topic("SOA")
@RequiresFacet(SwitchYardFacet.class)
@Help("Plugin for creating service-oriented applications with SwitchYard.")
public class SwitchYardPlugin extends AbstractPlugin {

    /**
     * List SwitchYard services available in the project.
     * @param out shell output
     */
    @Command(value = "list-services", help = "List services in this project.")
    public void listServices(final PipeOut out) {
        SwitchYardModel config = getProject().getFacet(SwitchYardFacet.class).getMergedSwitchYardConfig();
        out.println("[Public]");
        for (CompositeServiceModel service : config.getComposite().getServices()) {
            out.println(service.getName());
        }
        out.println("[Private]");
        for (ComponentModel component : config.getComposite().getComponents()) {
            for (ComponentServiceModel service : component.getServices()) {
                out.println(service.getName() 
                        + out.renderColor(ShellColor.BOLD, " : ")
                        + out.renderColor(ShellColor.YELLOW, service.getComponent().getName()));
            }
        }
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
        
        SwitchYardFacet switchYard = getProject().getFacet(SwitchYardFacet.class);
        
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
        service.setPromote(new QName(serviceName));
        switchYard.getSwitchYardConfig().getComposite().addService(service);
        
        // Save configuration changes
        switchYard.saveConfig();
        out.println("Promoted service " + serviceName);
    }
}
