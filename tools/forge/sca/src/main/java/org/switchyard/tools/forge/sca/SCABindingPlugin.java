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

package org.switchyard.tools.forge.sca;

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
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.SCABindingModel;
import org.switchyard.config.model.composite.v1.V1SCABindingModel;
import org.switchyard.config.model.switchyard.SwitchYardNamespace;
import org.switchyard.tools.forge.common.CommonFacet;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Forge commands related to SCA bindings.
 */
@Alias("sca-binding")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, CommonFacet.class, SCAFacet.class})
@Topic("SOA")
@Help("Provides commands to manage SCA bindings in SwitchYard.")
public class SCABindingPlugin implements Plugin {

    @Inject
    private Project _project;
    
    /**
     * Add a SCA binding to a SwitchYard service.
     * @param serviceName name of the service to bind
     * @param out shell output
     */
    @Command(value = "bind-service", help = "Add an SCA binding to a service.")
    public void bindService(
            @Option(required = true,
                    name = "serviceName",
                    description = "The service name") 
            final String serviceName,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeServiceModel service = switchYard.getCompositeService(serviceName);
        // Check to see if the service is public
        if (service == null) {
            out.println(out.renderColor(ShellColor.RED, "No public service named: " + serviceName));
            return;
        }

        SCABindingModel binding = new V1SCABindingModel(SwitchYardNamespace.DEFAULT.uri());
        service.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.sca to service " + serviceName);
    }
    

    /**
     * Add an SCA binding to a SwitchYard reference.
     * @param referenceName name of the reference to bind
     * @param out shell output
     */
    @Command(value = "bind-reference", help = "Add an SCA binding to a reference.")
    public void bindReference(
            @Option(required = true,
                    name = "referenceName",
                    description = "The reference name") 
            final String referenceName,
            final PipeOut out) {

        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeReferenceModel reference = switchYard.getCompositeReference(referenceName);
        // Check to see if the service is public
        if (reference == null) {
            out.println(out.renderColor(ShellColor.RED, "No public reference named: " + referenceName));
            return;
        }

        SCABindingModel binding = new V1SCABindingModel(SwitchYardNamespace.DEFAULT.uri());
        reference.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.sca to reference " + referenceName);
    }
}
