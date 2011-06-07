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

import org.apache.camel.builder.RouteBuilder;
import org.jboss.seam.forge.parser.JavaParser;
import org.jboss.seam.forge.parser.java.Annotation;
import org.jboss.seam.forge.parser.java.JavaClass;
import org.jboss.seam.forge.parser.java.JavaInterface;
import org.jboss.seam.forge.project.facets.JavaSourceFacet;
import org.jboss.seam.forge.project.facets.MetadataFacet;
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
import org.switchyard.component.camel.Route;
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
    
    private static final String SERVICE_TOKEN = "${service.name}";
    private static final String ROUTE_TEMPLATE = 
        "public void configure() {\n"
        + "from(\"switchyard://" + SERVICE_TOKEN + "\")"
        +"}";
    
    private enum RouteType {
        JAVA, XML;
        
        static RouteType fromString(String typeStr) {
            if (JAVA.toString().equalsIgnoreCase(typeStr)) {
                return JAVA;
            } else if (XML.toString().equalsIgnoreCase(typeStr)) {
                return XML;
            } else {
                return null;
            }
        }
    }
    
    /**
     * Create a new Camel component service.
     * @param serviceName service name
     * @param out shell output
     * @param routeType type of the route (Java, XML)
     * @throws java.io.FileNotFoundException error locating class 
     */
    @Command(value = "create", help = "Created a new Camel service.")
    public void newRoute(
            @Option(required = true,
                    name = "serviceName",
                    description = "The service name") 
            final String serviceName,
            @Option(required = false,
                    name = "type",
                    description = "Route type") 
            final String routeType,
            final PipeOut out) throws java.io.FileNotFoundException {

        RouteType type = RouteType.fromString(routeType);
        if (type == null || type == RouteType.JAVA) {
            createJavaRoute(serviceName, out);
        } else if (RouteType.XML == type) {
            createXMLRoute(serviceName);
        }
        
        
        out.println("Created Camel service " + serviceName);
    }
    
    private void createXMLRoute(String routeName) {
        SwitchYardFacet switchYard = getProject().getFacet(SwitchYardFacet.class);
        // Create the component service model
        V1ComponentModel component = new V1ComponentModel();
        component.setName(routeName + "Component");
        V1ComponentServiceModel service = new V1ComponentServiceModel();
        service.setName(routeName);
        component.addService(service);
        
        // Create the Camel implementation model and add it to the component model
        V1CamelImplementationModel impl = new V1CamelImplementationModel();
        component.setImplementation(impl);
        
        // Add the new component service to the application config
        SwitchYardModel syConfig = switchYard.getSwitchYardConfig();
        syConfig.getComposite().addComponent(component);
        switchYard.saveConfig();
    }
    
    /**
     * Creates a Java DSL bean containing a Camel route.  You'll notice that this
     * code is very similar to the Bean generation logic in the bean component
     * plugin.  We need to look at ways to synchronize these two pieces (e.g.
     * create a bean component, then add a route definition to it).
     */
    private void createJavaRoute(String routeName, PipeOut out) 
    throws java.io.FileNotFoundException {
        JavaSourceFacet java = getShell().getCurrentProject().getFacet(JavaSourceFacet.class);
        
        String pkgName = getProject().getFacet(MetadataFacet.class).getTopLevelPackage();
        
        if (pkgName == null) {
            pkgName = getShell().promptCommon(
                "Java package for route interface and implementation:",
                PromptType.JAVA_PACKAGE);
        }
        
        // Create the route interface
        JavaInterface routeInterface = JavaParser.create(JavaInterface.class)
            .setPackage(pkgName)
            .setName(routeName)
            .setPublic();
        java.saveJavaSource(routeInterface);
        
        // Now create the service implementation
        JavaClass routeClass = JavaParser.create(JavaClass.class)
            .setPackage(pkgName)
            .setName(routeName + "Builder")
            .setSuperType(RouteBuilder.class)
            .setPublic();
        
        Annotation<JavaClass> routeAnnotation = routeClass.addAnnotation(Route.class);
        routeAnnotation.setLiteralValue(routeInterface.getName() + ".class");
        
        routeClass.addMethod(ROUTE_TEMPLATE.replace(SERVICE_TOKEN, routeInterface.getName()));
        java.saveJavaSource(routeClass);
          
        // Notify user of success
        out.println("Created route interface [" + routeInterface.getName() + "]");
        out.println("Created route implementation [" + routeClass.getName() + "]");
        out.println(out.renderColor(ShellColor.BLUE, 
                "NOTE: Run 'mvn package' to make " + routeName + " visible to SwitchYard shell."));
    }
}
