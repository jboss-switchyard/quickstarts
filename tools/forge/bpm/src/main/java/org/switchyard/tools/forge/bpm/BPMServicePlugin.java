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

package org.switchyard.tools.forge.bpm;

import java.io.File;

import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.Topic;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.common.type.Classes;
import org.switchyard.component.bpm.config.model.v1.V1BpmComponentImplementationModel;
import org.switchyard.config.model.composite.JavaComponentServiceInterfaceModel;
import org.switchyard.config.model.composite.v1.V1ComponentModel;
import org.switchyard.config.model.composite.v1.V1ComponentServiceModel;
import org.switchyard.config.model.composite.v1.V1JavaComponentServiceInterfaceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Forge plugin for Bean component commands.
 */
@Alias("bpm-service")
@RequiresProject
@RequiresFacet({BPMFacet.class, ResourceFacet.class})
@Topic("SOA")
@Help("Provides commands related to BPM services in SwitchYard.")
public class BPMServicePlugin implements Plugin {

    // process definition template
    private static final String PROCESS_TEMPLATE = "ProcessTemplate.bpmn";
    // process definition file extension
    private static final String PROCESS_EXTENSION = ".bpmn";
    // process definition directory
    private static final String PROCESS_DIR = "META-INF";
    // VAR_* constants reference substitution tokens in the process definition template 
    private static final String VAR_SERVICE_NAME = "${service.name}";
    private static final String VAR_PACKAGE_NAME = "${package.name}";
    private static final String VAR_PROCESS_ID   = "${process.id}";
    
    @Inject
    private Project _project;
    
    @Inject
    private Shell _shell;
    
    /**
     * Create a new BPM service interface and implementation.
     * @param argServiceName service name
     * @param out shell output
     * @param argInterfaceClass class name of Java service interface
     * @param argProcessFilePath path to the BPMN process definition
     * @param argProcessId business process id
     * @throws java.io.IOException error with file resources
     */
    @Command(value = "create", help = "Created a new service backed by a BPM process.")
    public void newProcess(
            @Option(required = true,
                     name = "serviceName",
                     description = "The service name") 
             final String argServiceName,
             @Option(required = false,
                     name = "interfaceClass",
                     description = "The Java service interface") 
             final String argInterfaceClass,
             @Option(required = false,
                     name = "processDefinition",
                     description = "The business process definition") 
             final String argProcessFilePath,
             @Option(required = false,
                     name = "processId",
                     description = "The business process id") 
                     final String argProcessId,
             final PipeOut out)
    throws java.io.IOException {
      
        JavaSourceFacet java = _shell.getCurrentProject().getFacet(JavaSourceFacet.class);
        String pkgName = _project.getFacet(MetadataFacet.class).getTopLevelPackage();
        String interfaceClass = argInterfaceClass;
        
        if (interfaceClass == null) {
            // Figure out the Java package name for the interface
            if (pkgName == null) {
                pkgName = _shell.promptCommon(
                    "Java package for service interface:",
                    PromptType.JAVA_PACKAGE);
            }
        
            // Create the service interface
            JavaInterface processInterface = JavaParser.create(JavaInterface.class)
                .setPackage(pkgName)
                .setName(argServiceName)
                .setPublic();
            java.saveJavaSource(processInterface);
            interfaceClass = processInterface.getQualifiedName();

            out.println("Created service interface [" + interfaceClass + "]");
        }
        
        String processId = argProcessId;
        if (processId == null) {
            processId = argServiceName;
        }
        
        String processDefinitionPath = argProcessFilePath;
        if (processDefinitionPath == null) {
            // Create an empty process definition
            processDefinitionPath = PROCESS_DIR + File.separator + argServiceName + PROCESS_EXTENSION;
            String processDefinition = new StringPuller().pull(Classes.getResourceAsStream(PROCESS_TEMPLATE));
            processDefinition = processDefinition
                    .replaceAll(regexFormat(VAR_SERVICE_NAME), argServiceName)
                    .replaceAll(regexFormat(VAR_PROCESS_ID), processId)
                    .replaceAll(regexFormat(VAR_PACKAGE_NAME), pkgName);
            
            FileResource<?> processFile = _project.getFacet(ResourceFacet.class).getResource(processDefinitionPath);
            processFile.setContents(processDefinition);
            
            out.println("Created process definition [" + processDefinitionPath + "]");
        }
        
        // Add the SwitchYard config
        createImplementationConfig(argServiceName, interfaceClass, processId, processDefinitionPath);
          
        // Notify user of success
        out.println("Process service " + argServiceName + " has been created.");
    }
    
    private void createImplementationConfig(String serviceName,
            String interfaceName,
            String processId,
            String processDefinition) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        // Create the component service model
        V1ComponentModel component = new V1ComponentModel();
        component.setName(serviceName);
        V1ComponentServiceModel service = new V1ComponentServiceModel();
        service.setName(serviceName);
        JavaComponentServiceInterfaceModel csi = new V1JavaComponentServiceInterfaceModel();
        csi.setInterface(interfaceName);
        service.setInterface(csi);
        component.addService(service);
        
        // Create the BPM implementation model and add it to the component model
        V1BpmComponentImplementationModel bpm = new V1BpmComponentImplementationModel();
        bpm.setProcessDefinition(new SimpleResource(processDefinition));
        bpm.setProcessId(processId);
        component.setImplementation(bpm);
        
        // Add the new component service to the application config
        SwitchYardModel syConfig = switchYard.getSwitchYardConfig();
        syConfig.getComposite().addComponent(component);
        switchYard.saveConfig();
    }

    private String regexFormat(String token) {
        return token.replaceAll("\\$", "\\\\\\$")
                .replaceAll("\\{", "\\\\\\{")
                .replaceAll("\\}", "\\\\\\}");
    }
}
