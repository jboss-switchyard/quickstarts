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
    private static final String BEAN_INTERFACE_TEMPLATE = "java/BeanInterfaceTemplate.java";
    private static final String BEAN_IMPLEMENTATION_TEMPLATE = "java/BeanImplementationTemplate.java";
    
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
