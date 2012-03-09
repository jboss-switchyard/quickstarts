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

import static org.switchyard.component.bpm.ProcessConstants.MESSAGE_CONTENT_IN;
import static org.switchyard.component.bpm.ProcessConstants.MESSAGE_CONTENT_IN_NAME;
import static org.switchyard.component.bpm.ProcessConstants.MESSAGE_CONTENT_OUT;
import static org.switchyard.component.bpm.ProcessConstants.MESSAGE_CONTENT_OUT_NAME;
import static org.switchyard.component.bpm.task.work.SwitchYardServiceTaskHandler.SWITCHYARD_SERVICE;

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
import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.component.bpm.config.model.v1.V1BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1TaskHandlerModel;
import org.switchyard.component.bpm.task.work.SwitchYardServiceTaskHandler;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.composite.v1.V1ComponentModel;
import org.switchyard.config.model.composite.v1.V1ComponentServiceModel;
import org.switchyard.config.model.composite.v1.V1InterfaceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
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
    private static final String VAR_MESSAGE_CONTENT_IN_NAME = "${message.content.in.name}";
    private static final String VAR_MESSAGE_CONTENT_OUT_NAME = "${message.content.out.name}";
    
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
     * @param argMessageContentInName process variable name for the content of the incoming message
     * @param argMessageContentOutName process variable name for the content of the outgoing message
     * @param argAgent whether to use an agent
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
                    name = MESSAGE_CONTENT_IN_NAME,
                    description="The process variable name for the content of the incoming message (" + MESSAGE_CONTENT_IN + ")",
                    defaultValue = MESSAGE_CONTENT_IN)
            final String argMessageContentInName,
            @Option(required = false,
                    name = MESSAGE_CONTENT_OUT_NAME,
                    description="The process variable name for the content of the outgoing message (" + MESSAGE_CONTENT_OUT + ")",
                    defaultValue = MESSAGE_CONTENT_OUT)
            final String argMessageContentOutName,
            @Option(required = false,
                    name = "agent",
                    description = "Whether you want to use an agent to watch resources for changes (true|false)",
                    defaultValue = "false")
            final Boolean argAgent,
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
                .replaceToken(VAR_MESSAGE_CONTENT_IN_NAME, argMessageContentInName)
                .replaceToken(VAR_MESSAGE_CONTENT_OUT_NAME, argMessageContentOutName)
                .packageName(pkgName);
            template.writeResource(_project.getFacet(ResourceFacet.class).getResource(processDefinitionPath));
            
            out.println("Created process definition [" + processDefinitionPath + "]");
        }
        
        boolean agent = argAgent != null ? argAgent.booleanValue() : false;
        
        // Add the SwitchYard config
        createImplementationConfig(argServiceName, interfaceClass, processId, processDefinitionPath, argMessageContentInName, argMessageContentOutName, agent);
          
        // Notify user of success
        out.println("Process service " + argServiceName + " has been created.");
    }
    
    private void createImplementationConfig(String serviceName,
            String interfaceName,
            String processId,
            String processDefinition,
            String messageContentInName,
            String messageContentOutName,
            boolean agent) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        // Create the component service model
        V1ComponentModel component = new V1ComponentModel();
        component.setName(serviceName);
        V1ComponentServiceModel service = new V1ComponentServiceModel();
        service.setName(serviceName);
        InterfaceModel csi = new V1InterfaceModel(InterfaceModel.JAVA);
        csi.setInterface(interfaceName);
        service.setInterface(csi);
        component.addService(service);
        
        // Create the BPM implementation model and add it to the component model
        V1BPMComponentImplementationModel bpm = new V1BPMComponentImplementationModel();
        bpm.setProcessDefinition(new SimpleResource(processDefinition));
        bpm.setProcessId(processId);
        bpm.setMessageContentInName(messageContentInName);
        bpm.setMessageContentOutName(messageContentOutName);
        if (agent) {
            bpm.setAgent(true);
        }
        V1TaskHandlerModel switchyardHandler = new V1TaskHandlerModel();
        switchyardHandler.setName(SWITCHYARD_SERVICE);
        switchyardHandler.setClazz(SwitchYardServiceTaskHandler.class);
        bpm.addTaskHandler(switchyardHandler);
        component.setImplementation(bpm);
        
        // Add the new component service to the application config
        SwitchYardModel syConfig = switchYard.getSwitchYardConfig();
        syConfig.getComposite().addComponent(component);
        switchYard.saveConfig();
    }

}
