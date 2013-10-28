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

package org.switchyard.tools.forge.camel;

import java.util.Arrays;
import java.util.List;

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
import org.switchyard.component.camel.model.CamelNamespace;
import org.switchyard.component.camel.model.v1.V1CamelImplementationModel;
import org.switchyard.config.model.composite.v1.V1ComponentModel;
import org.switchyard.config.model.composite.v1.V1ComponentServiceModel;
import org.switchyard.config.model.composite.v1.V1InterfaceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.SwitchYardNamespace;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;
import org.switchyard.tools.forge.plugin.TemplateResource;

/**
 * Commands related to Camel services.
 */
@Alias("camel-service")
@RequiresProject
@RequiresFacet({SwitchYardFacet.class, CamelFacet.class})
@Topic("SOA")
@Help("Provides commands to create and edit Camel routes in SwitchYard.")
public class CamelServicePlugin implements Plugin {
    
    // Template files used for camel route services
    private static final String ROUTE_INTERFACE_TEMPLATE = 
            "/org/switchyard/tools/forge/camel/RouteInterfaceTemplate.java";
    private static final String ROUTE_XML_TEMPLATE = 
            "/org/switchyard/tools/forge/camel/RouteXmlTemplate.xml";
    private static final String ROUTE_IMPLEMENTATION_TEMPLATE = 
            "/org/switchyard/tools/forge/camel/RouteImplementationTemplate.java";
    
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
    
    @Inject
    private Project _project;
    
    @Inject
    private Shell _shell;
    
    
    /**
     * Create a new Camel component service.
     * @param serviceName service name
     * @param out shell output
     * @param routeType type of the route (Java, XML)
     * @throws java.io.IOException error locating class 
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
            final PipeOut out) throws java.io.IOException {

        RouteType type = RouteType.fromString(routeType);
        if (type == null || type == RouteType.JAVA) {
            createJavaRoute(serviceName, out);
        } else if (RouteType.XML == type) {
            createXMLRoute(serviceName, out);
        }
        
        
        out.println("Created Camel service " + serviceName);
    }
    
    private void createXMLRoute(String routeName, PipeOut out) throws java.io.IOException {
        // Gather interface details
        List<String> typeList = Arrays.asList(new String[] {"wsdl", "java"});
        int typeChoice = _shell.promptChoice("Interface type: ", typeList);
        String intfValue;
        if (typeList.get(typeChoice).equals("wsdl")) {
            String wsdlPath = _shell.prompt("WSDL path (ex. wsdl/MyService.wsdl): ");
            String wsdlPort = _shell.prompt("WSDL portType (ex. MyService): ");
            intfValue = wsdlPath + "#wsdl.porttype(" + wsdlPort + ")";
        } else {
            intfValue = _shell.prompt("Interface class (ex. org.foo.MyService): ");
        }
        
        // Create the component service model
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        V1ComponentModel component = new V1ComponentModel();
        component.setName(routeName + "Component");
        V1ComponentServiceModel service = new V1ComponentServiceModel(SwitchYardNamespace.DEFAULT.uri());
        service.setName(routeName);
        component.addService(service);
        V1InterfaceModel intfModel = new V1InterfaceModel(typeList.get(typeChoice));
        intfModel.setInterface(intfValue);
        service.setInterface(intfModel);
        
        // Create the Camel implementation model and XML route
        V1CamelImplementationModel impl = new V1CamelImplementationModel(CamelNamespace.DEFAULT.uri());
        TemplateResource xmlRoute = new TemplateResource(ROUTE_XML_TEMPLATE);
        String routeFile = routeName + ".xml";
        xmlRoute.serviceName(routeName);
        xmlRoute.writeResource(_project.getFacet(ResourceFacet.class).getResource(routeFile));
        out.println("Created Camel route definition [" + routeFile + "]");
        impl.setXMLPath(routeFile);
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
            throws java.io.IOException {
        
        String pkgName = _project.getFacet(MetadataFacet.class).getTopLevelPackage();
        
        if (pkgName == null) {
            pkgName = _shell.promptCommon(
                "Java package for route interface and implementation:",
                PromptType.JAVA_PACKAGE);
        }
        
        // Create the camel interface and implementation
        TemplateResource camelIntf = new TemplateResource(ROUTE_INTERFACE_TEMPLATE);
        camelIntf.serviceName(routeName);
        String interfaceFile = camelIntf.writeJavaSource(
                _project.getFacet(ResourceFacet.class), pkgName, routeName, false);
        out.println("Created route service interface [" + interfaceFile + "]");
        
        TemplateResource camelImpl = new TemplateResource(ROUTE_IMPLEMENTATION_TEMPLATE);
        camelImpl.serviceName(routeName);
        String implementationFile = camelImpl.writeJavaSource(
                _project.getFacet(ResourceFacet.class), pkgName, routeName + "Builder", false);
        out.println("Created route service implementation [" + implementationFile + "]");
        
        out.println(out.renderColor(ShellColor.BLUE, 
                "NOTE: Run 'mvn package' to make " + routeName + " visible to SwitchYard shell."));
    }
}
