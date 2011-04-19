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

import java.io.FileNotFoundException;

import org.jboss.seam.forge.parser.JavaParser;
import org.jboss.seam.forge.parser.java.Annotation;
import org.jboss.seam.forge.parser.java.JavaClass;
import org.jboss.seam.forge.parser.java.JavaInterface;
import org.jboss.seam.forge.project.facets.JavaSourceFacet;
import org.jboss.seam.forge.shell.PromptType;
import org.jboss.seam.forge.shell.ShellColor;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.Command;
import org.jboss.seam.forge.shell.plugins.Help;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.PipeOut;
import org.jboss.seam.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.shell.plugins.RequiresProject;
import org.jboss.seam.forge.shell.plugins.Topic;
import org.switchyard.component.bean.Service;
import org.switchyard.tools.forge.AbstractPlugin;

/**
 * Forge plugin for Bean component commands.
 */
@Alias("bean-service")
@RequiresProject
@RequiresFacet({BeanFacet.class})
@Topic("SOA")
@Help("Provides commands to create, edit, and remove CDI-based services in SwitchYard.")
public class BeanServicePlugin extends AbstractPlugin {
    
    /**
     * Create a new Bean service interface and implementation.
     * @param serviceName service name
     * @param out shell output
     * @throws FileNotFoundException trouble reading/writing SwitchYard config
     */
    @Command(value = "create", help = "Created a new service backed by a CDI bean.")
    public void newBean(
            @Option(required = true,
                     name = "serviceName",
                     description = "The service name") 
             final String serviceName,
             final PipeOut out)
    throws FileNotFoundException {
      
        JavaSourceFacet java = getShell().getCurrentProject().getFacet(JavaSourceFacet.class);
        
        String pkgName = getShell().promptCommon(
                "Java package for service interface and implementation:",
                PromptType.JAVA_PACKAGE);
        
        // Create the service interface
        JavaInterface beanInterface = JavaParser.create(JavaInterface.class)
            .setPackage(pkgName)
            .setName(serviceName)
            .setPublic();
        java.saveJavaSource(beanInterface);
        
        // Now create the service implementation
        JavaClass beanClass = JavaParser.create(JavaClass.class)
            .setPackage(pkgName)
            .setName(serviceName + "Bean")
            .addInterface(beanInterface)
            .setPublic();
        Annotation<JavaClass> serviceAnnotation = beanClass.addAnnotation(Service.class);
        serviceAnnotation.setLiteralValue(beanInterface.getName() + ".class");
        java.saveJavaSource(beanClass);
          
        // Notify user of success
        out.println("Created service interface [" + beanInterface.getName() + "]");
        out.println("Created service implementation [" + beanClass.getName() + "]");
        out.println(out.renderColor(ShellColor.BLUE, 
                "NOTE: Run 'mvn package' to make " + serviceName + " visible to SwitchYard shell."));
    }
}
