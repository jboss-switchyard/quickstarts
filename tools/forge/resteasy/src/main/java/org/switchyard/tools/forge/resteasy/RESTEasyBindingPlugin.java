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

package org.switchyard.tools.forge.resteasy;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.MetadataFacet;
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
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.component.resteasy.config.model.v1.V1RESTEasyBindingModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;
import org.switchyard.tools.forge.common.CommonFacet;

/**
 * Forge commands related to RESTEasy bindings.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@Alias("rest-binding")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, CommonFacet.class, RESTEasyFacet.class})
@Topic("SOA")
@Help("Provides commands to manage RESTEasy service bindings in SwitchYard.")
public class RESTEasyBindingPlugin implements Plugin {

    @Inject
    private Project _project;
    
    /**
     * Add a RESTEasy binding to a SwitchYard service.
     * @param serviceName name of the reference to bind
     * @param interfaces a set of interface classes to configure the RESTEasy binding
     * @param contextPath the additional context path where the REST endpoints will be hosted
     * @param out shell output
     */
    @Command(value = "bind-service", help = "Add a RESTEasy binding to a service.")
    public void bindService(
            @Option(required = true,
                    name = "serviceName",
                    description = "The service name")
            final String serviceName,
            @Option(required = true,
                    name = "interfaces",
                    description = "A comma seperated list of interface/abstract/empty classes with JAX-RS annotations")
            final String interfaces,
            @Option(required = false,
                    name = "contextPath",
                    description = "The context root for this RESTEasy endpoint, defaults to [projectName]")
            final String contextPath,
            final PipeOut out) {

        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeServiceModel service = switchYard.getCompositeService(serviceName);
        // Check to see if the service is public
        if (service == null) {
            out.println(out.renderColor(ShellColor.RED, "No public service named: " + serviceName));
            return;
        }

        RESTEasyBindingModel binding = new V1RESTEasyBindingModel();
        binding.setInterfaces(interfaces);
        String projectName = _project.getFacet(MetadataFacet.class).getProjectName();
        if ((contextPath != null) && contextPath.length() > 0) {
            binding.setContextPath(contextPath);
        } else {
            binding.setContextPath(projectName);
        }
        service.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.rest to service " + serviceName);
    }

    /**
     * Add a RESTEasy binding to a SwitchYard reference.
     * @param referenceName name of the reference to bind
     * @param interfaces a set of interface classes to configure the RESTEasy binding
     * @param address optional value of the root url where the REST endpoints are hosted
     * @param contextPath optional value of additional context path
     * @param out shell output
     */
    @Command(value = "bind-reference", help = "Add a RESTEasy binding to a reference.")
    public void bindReference(
            @Option(required = true,
                    name = "referenceName",
                    description = "The reference name")
            final String referenceName,
            @Option(required = true,
                    name = "interfaces",
                    description = "A comma seperated list of interface/abstract/empty classes with JAX-RS annotations")
            final String interfaces,
            @Option(required = false,
                    name = "address",
                    description = "The remote REST enpoints address, defaults to localhost:8080")
            final String address,
            @Option(required = false,
                    name = "contextPath",
                    description = "Additional context for the remote RESTEasy endpoint, defaults to [projectName]")
            final String contextPath,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeReferenceModel reference = switchYard.getCompositeReference(referenceName);
        // Check to see if the service is public
        if (reference == null) {
            out.println(out.renderColor(ShellColor.RED, "No public reference named: " + referenceName));
            return;
        }

        RESTEasyBindingModel binding = new V1RESTEasyBindingModel();
        binding.setInterfaces(interfaces);
        if (address != null) {
            binding.setAddress(address);
        }
        String projectName = _project.getFacet(MetadataFacet.class).getProjectName();
        if (contextPath != null) {
            binding.setContextPath(contextPath);
        } else if (address == null) {
            binding.setContextPath(projectName);
        }
        reference.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.rest to reference " + referenceName);
    }
}
