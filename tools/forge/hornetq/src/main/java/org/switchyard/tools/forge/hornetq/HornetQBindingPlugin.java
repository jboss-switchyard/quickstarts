/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.tools.forge.hornetq;

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
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.HornetQConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQConnectorConfigModel;
import org.switchyard.component.hornetq.config.model.v1.V1ConnectorConfigModel;
import org.switchyard.component.hornetq.config.model.v1.V1HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.v1.V1HornetQConfigModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;
import org.switchyard.tools.forge.common.CommonFacet;

/**
 * Forge commands related to HornetQ bindings.
 */
@Alias("hornetq-binding")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, CommonFacet.class, HornetQFacet.class})
@Topic("SOA")
@Help("Provides commands to manage HornetQ service bindings in SwitchYard.")
public class HornetQBindingPlugin implements Plugin {

    @Inject
    private Project _project;
    
    /**
     * Add a HornetQ binding to a SwitchYard service.
     * @param serviceName name of the reference to bind
     * @param queueName The queue name
     * @param factoryClass The ConnectorFactory class name
     * @param out shell output
     */
    @Command(value = "bind-service", help = "Add a HornetQ binding to a service.")
    public void bindService(
            @Option(required = true,
                    name = "serviceName",
                    description = "The service name") 
            final String serviceName,
            @Option(required = true,
                    name = "queueName",
                    description = "The queue name") 
            final String queueName,
            @Option(required = false,
                    name = "factoryClass",
                    defaultValue = "org.hornetq.core.remoting.impl.invm.InVMConnectorFactory",
                    description = "The ConnectorFactory class name")
            final String factoryClass,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeServiceModel service = switchYard.getCompositeService(serviceName);
        // Check to see if the service is public
        if (service == null) {
            out.println(out.renderColor(ShellColor.RED, "No public service named: " + serviceName));
            return;
        }

        HornetQBindingModel binding = new V1HornetQBindingModel();
        HornetQConfigModel config = new V1HornetQConfigModel();
        HornetQConnectorConfigModel connector = new V1ConnectorConfigModel();
        connector.setConnectorClassName(factoryClass);
        config.setConnectorConfiguration(connector);
        config.setQueue(queueName);
        binding.setHornetQConfig(config);
        service.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.hornetq to service " + serviceName);
    }
    

    /**
     * Add a HornetQ binding to a SwitchYard reference.
     * @param referenceName name of the reference to bind
     * @param queueName The queue name
     * @param factoryClass The ConnectorFactory class name
     * @param out shell output
     */
    @Command(value = "bind-reference", help = "Add a SOAP binding to a reference.")
    public void bindReference(
            @Option(required = true,
                    name = "referenceName",
                    description = "The reference name") 
            final String referenceName,
            @Option(required = true,
                    name = "queueName",
                    description = "The queue name") 
            final String queueName,
            @Option(required = false,
                    name = "factoryClass",
                    defaultValue = "org.hornetq.core.remoting.impl.invm.InVMConnectorFactory",
                    description = "The ConnectorFactory class name")
            final String factoryClass,
            final PipeOut out) {

        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeReferenceModel reference = switchYard.getCompositeReference(referenceName);
        // Check to see if the service is public
        if (reference == null) {
            out.println(out.renderColor(ShellColor.RED, "No public reference named: " + referenceName));
            return;
        }

        HornetQBindingModel binding = new V1HornetQBindingModel();
        HornetQConfigModel config = new V1HornetQConfigModel();
        HornetQConnectorConfigModel connector = new V1ConnectorConfigModel();
        connector.setConnectorClassName(factoryClass);
        config.setConnectorConfiguration(connector);
        config.setQueue(queueName);
        binding.setHornetQConfig(config);
        
        reference.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.hornetq to reference " + referenceName);
    }
}
