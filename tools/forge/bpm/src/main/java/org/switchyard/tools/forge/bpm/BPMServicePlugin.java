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
package org.switchyard.tools.forge.bpm;

import java.io.File;

import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.ResourceFacet;
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
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.component.bpm.BPMOperationType;
import org.switchyard.component.bpm.config.model.BPMNamespace;
import org.switchyard.component.bpm.config.model.v1.V1BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1BPMOperationModel;
import org.switchyard.component.common.knowledge.config.model.OperationModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1ManifestModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1OperationsModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.composite.v1.V1ComponentModel;
import org.switchyard.config.model.composite.v1.V1ComponentServiceModel;
import org.switchyard.config.model.composite.v1.V1InterfaceModel;
import org.switchyard.config.model.resource.v1.V1ResourceModel;
import org.switchyard.config.model.resource.v1.V1ResourcesModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.SwitchYardNamespace;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;
import org.switchyard.tools.forge.plugin.TemplateResource;

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
    private static final String VAR_PROCESS_ID   = "${process.id}";
    
    @Inject
    private Project _project;
    
    @Inject
    private Shell _shell;
    
    /**
     * Create a new BPM service interface and implementation.
     * @param argServiceName service name
     * @param argInterfaceClass class name of Java service interface
     * @param argProcessFilePath path to the BPMN process definition
     * @param argProcessId business process id
     * @param argPersistent persistent flag
     * @param out shell output
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
            @Option(required = false,
            name = "persistent",
            description = "The persistent flag")
            final boolean argPersistent,
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

            // Add a default process method...
            processInterface.addMethod("void process(String content);");

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
            TemplateResource template = new TemplateResource(PROCESS_TEMPLATE)
                .serviceName(argServiceName)
                .replaceToken(VAR_PROCESS_ID, processId)
                .packageName(pkgName);
            template.writeResource(_project.getFacet(ResourceFacet.class).getResource(processDefinitionPath));
            
            out.println("Created process definition [" + processDefinitionPath + "]");
        }
        
        // Add the SwitchYard config
        createImplementationConfig(argServiceName, interfaceClass, processId, argPersistent, processDefinitionPath);
          
        // Notify user of success
        out.println("Process service " + argServiceName + " has been created.");
    }
    
    private void createImplementationConfig(String serviceName,
            String interfaceName,
            String processId,
            boolean persistent,
            String processDefinition) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        // Create the component service model
        V1ComponentModel component = new V1ComponentModel();
        component.setName(serviceName);
        V1ComponentServiceModel service = new V1ComponentServiceModel(SwitchYardNamespace.DEFAULT.uri());
        service.setName(serviceName);
        InterfaceModel csi = new V1InterfaceModel(InterfaceModel.JAVA);
        csi.setInterface(interfaceName);
        service.setInterface(csi);
        component.addService(service);
        
        // Create the BPM implementation model and add it to the component model
        V1BPMComponentImplementationModel bpm = new V1BPMComponentImplementationModel(BPMNamespace.DEFAULT.uri());
        bpm.setProcessId(processId);
        bpm.setPersistent(persistent);
        V1OperationsModel operations = new V1OperationsModel(BPMNamespace.DEFAULT.uri());
        OperationModel operation = (OperationModel)new V1BPMOperationModel(BPMNamespace.DEFAULT.uri()).setType(BPMOperationType.START_PROCESS).setName("operation");
        operations.addOperation(operation);
        bpm.setOperations(operations);
        V1ManifestModel manifest = new V1ManifestModel(BPMNamespace.DEFAULT.uri());
        V1ResourcesModel resources = new V1ResourcesModel(BPMNamespace.DEFAULT.uri());
        resources.addResource(new V1ResourceModel(BPMNamespace.DEFAULT.uri()).setLocation(processDefinition).setType(ResourceType.valueOf("BPMN2")));
        manifest.setResources(resources);
        bpm.setManifest(manifest);
        component.setImplementation(bpm);
        
        // Add the new component service to the application config
        SwitchYardModel syConfig = switchYard.getSwitchYardConfig();
        syConfig.getComposite().addComponent(component);
        switchYard.saveConfig();
    }

}
