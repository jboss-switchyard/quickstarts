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

package org.switchyard.tools.forge.http;

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
import org.switchyard.component.http.config.model.BasicAuthModel;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.component.http.config.model.NtlmAuthModel;
import org.switchyard.component.http.config.model.v1.V1BasicAuthModel;
import org.switchyard.component.http.config.model.v1.V1HttpBindingModel;
import org.switchyard.component.http.config.model.v1.V1NtlmAuthModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;
import org.switchyard.tools.forge.common.CommonFacet;

/**
 * Forge commands related to HTTP bindings.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@Alias("http-binding")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, CommonFacet.class, HttpFacet.class})
@Topic("SOA")
@Help("Provides commands to manage HTTP service bindings in SwitchYard.")
public class HttpBindingPlugin implements Plugin {

    @Inject
    private Project _project;
    
    /**
     * Add a HTTP binding to a SwitchYard service.
     * @param serviceName name of the reference to bind
     * @param contextPath the context path where the HTTP endpoints will be hosted
     * @param out shell output
     */
    @Command(value = "bind-service", help = "Add a HTTP binding to a service.")
    public void bindService(
            @Option(required = true,
                    name = "serviceName",
                    description = "The service name") 
            final String serviceName,
            @Option(required = false,
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

        HttpBindingModel binding = new V1HttpBindingModel();
        String projectName = _project.getFacet(MetadataFacet.class).getProjectName();
        if ((contextPath != null) && contextPath.length() > 0) {
            binding.setContextPath(contextPath);
        } else {
            binding.setContextPath(projectName);
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
     * @param user optional value of user name
     * @param password optional value of password
     * @param realm optional value of authentication realm
     * @param domain optional value of authentication domain
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
            @Option(required = false,
                    name = "user",
                    description = "The user name for authentication") 
            final String user,
            @Option(required = false,
                    name = "password",
                    description = "The password for authentication") 
            final String password,
            @Option(required = false,
                    name = "realm",
                    description = "The realm for authentication") 
            final String realm,
            @Option(required = false,
                    name = "domain",
                    description = "The NTLM domain for authentication") 
            final String domain,
            final PipeOut out) {

        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        CompositeReferenceModel reference = switchYard.getCompositeReference(referenceName);
        // Check to see if the service is public
        if (reference == null) {
            out.println(out.renderColor(ShellColor.RED, "No public reference named: " + referenceName));
            return;
        }

        HttpBindingModel binding = new V1HttpBindingModel();
        if (address != null) {
            binding.setAddress(address);
        }
        if (method != null) {
            binding.setMethod(method);
        }
        if (contentType != null) {
            binding.setContentType(contentType);
        }
        if (domain != null) {
            NtlmAuthModel ntlm= new V1NtlmAuthModel();
            ntlm.setUser(user);
            ntlm.setPassword(password);
            ntlm.setRealm(realm);
            ntlm.setDomain(domain);
            binding.setNtlmAuthConfig(ntlm);
        } else {
            BasicAuthModel basic= new V1BasicAuthModel();
            basic.setUser(user);
            basic.setPassword(password);
            basic.setRealm(realm);
            binding.setBasicAuthConfig(basic);
        }
        reference.addBinding(binding);

        switchYard.saveConfig();
        out.println("Added binding.http to reference " + referenceName);
    }
}
