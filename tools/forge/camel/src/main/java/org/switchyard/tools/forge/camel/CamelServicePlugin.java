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

import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.Command;
import org.jboss.seam.forge.shell.plugins.Help;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.PipeOut;
import org.jboss.seam.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.shell.plugins.RequiresProject;
import org.jboss.seam.forge.shell.plugins.Topic;
import org.switchyard.component.camel.config.model.v1.V1CamelImplementationModel;
import org.switchyard.config.model.composite.v1.V1ComponentModel;
import org.switchyard.config.model.composite.v1.V1ComponentServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.tools.forge.AbstractPlugin;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Commands related to Camel services.
 */
@Alias("camel-service")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, CamelFacet.class})
@Topic("SOA")
@Help("Provides commands to create and edit Camel routes in SwitchYard.")
public class CamelServicePlugin extends AbstractPlugin {
    
    /**
     * Create a new Camel component service.
     * @param serviceName service name
     * @param out shell output
     */
    @Command(value = "create", help = "Created a new Camel service.")
    public void newRoute(
            @Option(required = true,
                    name = "serviceName",
                    description = "The service name") 
            final String serviceName,
            final PipeOut out) {
        

        SwitchYardFacet switchYard = getProject().getFacet(SwitchYardFacet.class);
        
        // Create the component service model
        V1ComponentModel component = new V1ComponentModel();
        component.setName(serviceName + "Component");
        V1ComponentServiceModel service = new V1ComponentServiceModel();
        service.setName(serviceName);
        component.addService(service);
        
        // Create the Camel implementation model and add it to the component model
        V1CamelImplementationModel impl = new V1CamelImplementationModel();
        component.setImplementation(impl);
        
        // Add the new component service to the application config
        SwitchYardModel syConfig = switchYard.getSwitchYardConfig();
        syConfig.getComposite().addComponent(component);
        switchYard.saveConfig();
        out.println("Created Camel service " + serviceName);
    }
    
}
