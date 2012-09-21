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

package org.switchyard.tools.forge.http;

import java.lang.reflect.Method;

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
import org.switchyard.component.common.selector.config.model.v1.V1StaticOperationSelectorModel;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Forge commands related to HTTP bindings.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@Alias("http-binding")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, HttpFacet.class})
@Topic("SOA")
@Help("Provides commands to manage HTTP service bindings in SwitchYard.")
public class HttpBindingPlugin implements Plugin {

    @Inject
    private Project _project;
    
    /**
     * Add a HTTP binding to a SwitchYard service.
     * @param serviceName name of the reference to bind
     * @param contextPath the context path where the HTTP endpoints will be hosted
     * @param operationName target operation name for the SwitchYard service
     * @param out shell output
     */
    @Command(value = "bind-service", help = "Add a HTTP binding to a service.")
    public void bindService(
            @Option(required = true,
                    name = "serviceName",
                    description = "The service name") 
            final String serviceName,
            @Option(required = true,
                    name = "operationName",
                    description = "The operation name") 
            final String operationName,
            @Option(required = true,
                    name = "contextPath",
                    description = "The context root for this HTTP endpoint, defaults to [projectName]")
            final String contextPath,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeServiceModel service = switchYard.getCompositeService(serviceName);
        // Check to see if the service is public
        if (service == null) {
            out.println(out.renderColor(ShellColor.RED, "No public service named: " + serviceName));
            return;
        }

        HttpBindingModel binding = new HttpBindingModel();
        String projectName = _project.getFacet(MetadataFacet.class).getProjectName();
        if ((contextPath != null) && contextPath.length() > 0) {
            binding.setContextPath(contextPath);
        } else {
            binding.setContextPath(projectName);
        }
        // Add an operation selector if an operation name has been specified
        if (operationName != null) {
            V1StaticOperationSelectorModel operation = new V1StaticOperationSelectorModel();
            operation.setOperationName(operationName);
            binding.setOperationSelector(operation);
        }
        service.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.http to service " + serviceName);
    }
    

    /**
     * Add a HTTP binding to a SwitchYard reference.
     * @param referenceName name of the reference to bind
     * @param address required value of the root url where the HTTP endpoints are hosted
     * @param method optional value of HTTP method used to invoke the endpoint
     * @param contentType optional value of content type of the request body
     * @param out shell output
     */
    @Command(value = "bind-reference", help = "Add a SOAP binding to a reference.")
    public void bindReference(
            @Option(required = true,
                    name = "referenceName",
                    description = "The reference name") 
            final String referenceName,
            @Option(required = true,
                    name = "address",
                    description = "The remote HTTP endpoint's address")
            final String address,
            @Option(required = false,
                    name = "method",
                    description = "The HTTP method used for invoking the endpoint. Can be GET, POST, etc.,") 
            final String method,
            @Option(required = false,
                    name = "contentType",
                    description = "The HTTP Content-Type header value that identifies the body content") 
            final String contentType,
            final PipeOut out) {

        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeReferenceModel reference = switchYard.getCompositeReference(referenceName);
        // Check to see if the service is public
        if (reference == null) {
            out.println(out.renderColor(ShellColor.RED, "No public reference named: " + referenceName));
            return;
        }

        HttpBindingModel binding = new HttpBindingModel();
        if (address != null) {
            binding.setAddress(address);
        }
        if (method != null) {
            binding.setMethod(method);
        }
        if (contentType != null) {
            binding.setContentType(contentType);
        }
        reference.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.http to reference " + referenceName);
    }
}
