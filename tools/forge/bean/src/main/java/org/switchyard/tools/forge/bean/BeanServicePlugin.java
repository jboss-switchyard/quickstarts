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

package org.switchyard.tools.forge.bean;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
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
import org.switchyard.tools.forge.plugin.TemplateResource;

/**
 * Forge plugin for Bean component commands.
 */
@Alias("bean-service")
@RequiresProject
@RequiresFacet({BeanFacet.class})
@Topic("SOA")
@Help("Provides commands to create, edit, and remove CDI-based services in SwitchYard.")
public class BeanServicePlugin implements Plugin {
    
    // Template files used for bean services
    private static final String BEAN_INTERFACE_TEMPLATE = "/org/switchyard/tools/forge/bean/BeanInterfaceTemplate.java";
    private static final String BEAN_IMPLEMENTATION_TEMPLATE = "/org/switchyard/tools/forge/bean/BeanImplementationTemplate.java";
    
    @Inject
    private Project _project;
    
    @Inject
    private Shell _shell;
    
    /**
     * Create a new Bean service interface and implementation.
     * @param serviceName service name
     * @param out shell output
     * @throws java.io.IOException trouble reading/writing SwitchYard config
     */
    @Command(value = "create", help = "Created a new service backed by a CDI bean.")
    public void newBean(
            @Option(required = true,
                     name = "serviceName",
                     description = "The service name") 
             final String serviceName,
             final PipeOut out)
    throws java.io.IOException {
      
        String pkgName = _project.getFacet(MetadataFacet.class).getTopLevelPackage();
        
        if (pkgName == null) {
            pkgName = _shell.promptCommon(
                "Java package for service interface and implementation:",
                PromptType.JAVA_PACKAGE);
        }
        
        // Create the service interface and implementation
        TemplateResource beanIntf = new TemplateResource(BEAN_INTERFACE_TEMPLATE);
        beanIntf.serviceName(serviceName);
        String interfaceFile = beanIntf.writeJavaSource(
                _project.getFacet(ResourceFacet.class), pkgName, serviceName, false);
        out.println("Created service interface [" + interfaceFile + "]");
        
        TemplateResource beanImpl = new TemplateResource(BEAN_IMPLEMENTATION_TEMPLATE);
        beanImpl.serviceName(serviceName);
        String implementationFile = beanImpl.writeJavaSource(
                _project.getFacet(ResourceFacet.class), pkgName, serviceName + "Bean", false);
        out.println("Created service implementation [" + implementationFile + "]");
          
        // Notify user of success
        out.println(out.renderColor(ShellColor.BLUE, 
                "NOTE: Run 'mvn package' to make " + serviceName + " visible to SwitchYard shell."));
    }
}
