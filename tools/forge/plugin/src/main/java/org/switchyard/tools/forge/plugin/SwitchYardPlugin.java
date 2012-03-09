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
package org.switchyard.tools.forge.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

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
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.v1.V1ComponentReferenceModel;
import org.switchyard.config.model.composite.v1.V1CompositeReferenceModel;
import org.switchyard.config.model.composite.v1.V1CompositeServiceModel;
import org.switchyard.config.model.composite.v1.V1InterfaceModel;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.HandlerModel;
import org.switchyard.config.model.domain.HandlersModel;
import org.switchyard.config.model.domain.v1.V1DomainModel;
import org.switchyard.config.model.domain.v1.V1HandlerModel;
import org.switchyard.config.model.domain.v1.V1HandlersModel;
import org.switchyard.config.model.switchyard.ArtifactModel;
import org.switchyard.config.model.switchyard.ArtifactsModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1ArtifactModel;
import org.switchyard.config.model.switchyard.v1.V1ArtifactsModel;

/**
 * Project-level commands for SwitchYard applications.
 */
@Alias("switchyard")
@RequiresProject
@Topic("SOA")
@RequiresFacet(SwitchYardFacet.class)
@Help("Plugin for creating service-oriented applications with SwitchYard.")
public class SwitchYardPlugin implements Plugin {
    
    // Directory where artifacts are stored
    private static final String ARTIFACT_DIR = "lib";
    // Template file used for unit testing services
    //private static final String TEST_SERVICE_TEMPLATE = "java/TestTemplate.java";
    private static final String TEST_SERVICE_TEMPLATE = "/org/switchyard/tools/forge/plugin/TestTemplate.java";
    // MessageTrace handler name and class
    private static final String TRACE_CLASS = "org.switchyard.handlers.MessageTrace";
    private static final String TRACE_NAME = "MessageTrace";

    @Inject
    private Project _project;

    @Inject
    private Shell _shell;

    /**
     * List SwitchYard services available in the project.
     * @param verbose true to enable XML dump of config
     * @param out shell output
     */
    @Command(value = "show-config", help = "Show the current configuration state of the application.")
    public void listServices(
            @Option(required = false,
                     name = "verbose",
                     description = "Print the full application configuration as XML") 
            final Boolean verbose,
            final PipeOut out) {
        
        SwitchYardModel config = _project.getFacet(SwitchYardFacet.class).getMergedSwitchYardConfig();
        
        // 'verbose' option with no value or value=true counts
        if (verbose == null || verbose) {
            out.println(config.toString());
            return;
        }
        
        out.println();
        out.println("[Public]");
        // Print promoted service info
        for (CompositeServiceModel service : config.getComposite().getServices()) {
            out.print(out.renderColor(ShellColor.BOLD, "service: "));
            out.println(service.getName());
            out.print(out.renderColor(ShellColor.BOLD, "   interface: "));
            if (service.getInterface() != null) {
                out.println(service.getInterface().getInterface());
            } else {
                out.println(out.renderColor(ShellColor.YELLOW, "inherited"));
            }
            for (BindingModel binding : service.getBindings()) {
                out.print(out.renderColor(ShellColor.BOLD, "   binding: "));
                out.println(binding.getType());
            }
        }
        // Print promoted reference info
        for (CompositeReferenceModel reference : config.getComposite().getReferences()) {
            out.print(out.renderColor(ShellColor.BOLD, "reference: "));
            out.println(reference.getName());
            out.print(out.renderColor(ShellColor.BOLD, "   interface: "));
            if (reference.getInterface() != null) {
                out.println(reference.getInterface().getInterface());
            } else {
                out.println(out.renderColor(ShellColor.YELLOW, "inherited"));
            }
            for (BindingModel binding : reference.getBindings()) {
                out.print(out.renderColor(ShellColor.BOLD, "   binding: "));
                out.println(binding.getType());
            }
        }
        
        out.println();
        out.println("[Private]");
        for (ComponentModel component : config.getComposite().getComponents()) {
            out.print(out.renderColor(ShellColor.BOLD, "component: "));
            out.println(component.getName());
            for (ComponentServiceModel service : component.getServices()) {
                out.print(out.renderColor(ShellColor.BOLD, "   service: "));
                out.println(service.getName());
                out.print(out.renderColor(ShellColor.BOLD, "      interface: "));
                if (service.getInterface() != null) {
                    out.println(service.getInterface().getInterface());
                } else {
                    out.println(out.renderColor(ShellColor.RED, "unspecified"));
                }
            }
            for (ComponentReferenceModel reference : component.getReferences()) {
                out.print(out.renderColor(ShellColor.BOLD, "   reference: "));
                out.println(reference.getName());
                out.print(out.renderColor(ShellColor.BOLD, "      interface: "));
                if (reference.getInterface() != null) {
                    out.println(reference.getInterface().getInterface());
                } else {
                    out.println(out.renderColor(ShellColor.RED, "unspecified"));
                }
            }
        }
        out.println();
    }
    
    /**
     * Print SwitchYard version used in this application.
     * @param out shell output
     */
    @Command(value = "get-version", help = "Show the version of SwitchYard used by this application.")
    public void getVersion(final PipeOut out) {
        String version = _project.getFacet(SwitchYardFacet.class).getVersion();
        out.println("SwitchYard version " + version);
    }
    
    /**
     * Promote a component-level service to a composite-level service.
     * @param serviceName name of the service to promote
     * @param out shell output
     */
    @Command(value = "promote-service", help = "Promote a private service to public.")
    public void promoteService(
            @Option(required = true,
                     name = "serviceName",
                     description = "The service name") final String serviceName,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        
        // Check to see if the service is already promoted
        if (switchYard.getCompositeService(serviceName) != null) {
            out.println(out.renderColor(ShellColor.RED, "Service has already been promoted: " + serviceName));
            return;
        }
        // Make sure a component service exists
        if (switchYard.getComponentService(serviceName) == null) {
            out.println(out.renderColor(ShellColor.RED, "Component service not found: " + serviceName));
            return;
        }
        // Create the composite service
        V1CompositeServiceModel service = new V1CompositeServiceModel();
        service.setName(serviceName);
        service.setPromote(serviceName);
        switchYard.getSwitchYardConfig().getComposite().addService(service);
        
        // Save configuration changes
        switchYard.saveConfig();
        out.println("Promoted service " + serviceName);
    }
    

    /**
     * Promote a component-level reference to a composite-level reference.
     * @param referenceName name of the reference to promote
     * @param out shell output
     */
    @Command(value = "promote-reference", help = "Promote a private reference to public.")
    public void promoteReference(
            @Option(required = true,
                     name = "referenceName",
                     description = "The reference name") final String referenceName,
            final PipeOut out) {
        
        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        
        // Check to see if the service is already promoted
        if (switchYard.getCompositeReference(referenceName) != null) {
            out.println(out.renderColor(ShellColor.RED, "Reference has already been promoted: " + referenceName));
            return;
        }
        // Make sure a component service exists
        ComponentReferenceModel component = switchYard.getComponentReference(referenceName);
        if (component == null) {
            out.println(out.renderColor(ShellColor.RED, "Component reference not found: " + referenceName));
            return;
        }
        // Create the composite service
        V1CompositeReferenceModel reference = new V1CompositeReferenceModel();
        reference.setName(referenceName);
        reference.setPromote(component.getComponent().getName() + "/" + referenceName);
        switchYard.getSwitchYardConfig().getComposite().addReference(reference);
        
        // Save configuration changes
        switchYard.saveConfig();
        out.println("Promoted reference " + referenceName);
    }
    
    /**
     * Add a unit test for a service.
     * @param serviceName name of the service to test
     * @param out shell output
     * @throws java.io.IOException failed to create unit test file
     */
    @Command(value = "create-service-test", help = "Create a unit test for a SwitchYard service.")
    public void createServiceTest(
            @Option(required = true,
                     name = "serviceName",
                     description = "The service name") final String serviceName,
            final PipeOut out) throws java.io.IOException {
        
        String pkgName = _project.getFacet(MetadataFacet.class).getTopLevelPackage();
        if (pkgName == null) {
            pkgName = _shell.promptCommon(
                "Java package for service test:",
                PromptType.JAVA_PACKAGE);
        }
        
        TemplateResource template = new TemplateResource(TEST_SERVICE_TEMPLATE);
        template.serviceName(serviceName);
        String testFile = template.writeJavaSource(_project.getFacet(ResourceFacet.class), 
                pkgName, serviceName + "Test", true);
        
        
        out.println("Created unit test " + testFile);
    }
    
    /**
     * Adds or removes the message trace handler based on message tracing preference.
     * @param enable true to enable tracing, false to disable
     * @param out shell output
     */
    @Command(value = "trace-messages", help = "Enable tracing of messages moving between services")
    public void traceMessages(
            @Option(required = true,
                     name = "enableTrace",
                     description = "Set to true to enable tracing, false to disable.") 
            final Boolean enable,
            final PipeOut out) {

        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        DomainModel domain = switchYard.getSwitchYardConfig().getDomain();
        String result;
        
        // If enable option is not specified or enable=true, then enable the MessageTrace handler
        if (enable == null || enable) {
            // create the domain config if it doesn't exist already
            if (domain == null) {
                domain = new V1DomainModel();
                switchYard.getSwitchYardConfig().setDomain(domain);
            }
            // need to create the handlers config if it's not already present
            HandlersModel handlers = domain.getHandlers();
            if (handlers == null) {
                handlers = new V1HandlersModel();
                domain.setHandlers(handlers);
            }
            handlers.addHandler(new V1HandlerModel()
                .setClassName(TRACE_CLASS)
                .setName(TRACE_NAME));
            result = "Message tracing has been enabled.";
        } else {
            // Disable the handler by removing the configuration
            if (domain != null && domain.getHandlers() != null) {
                for (HandlerModel handler : domain.getHandlers().getHandlers()) {
                    if (TRACE_CLASS.equals(handler.getClass()) 
                        && TRACE_NAME.equals(handler.getName())) {
                        domain.getHandlers().removeHandler(TRACE_NAME);
                    }
                }
            }
            result = "Message tracing has been disabled.";
        }

        // Save configuration changes
        switchYard.saveConfig();
        out.println(result);
    }
    
    /**
     * Import the specified artifact into the application project.
     * @param urlStr url for the artifact module
     * @param name name of the artifact module
     * @param download true will attempt download of the artifact module
     * @param out shell output
     */
    @Command(value = "import-artifacts", help = "Import service artifacts into project")
    public void importArtifacts(
            @Option(required = true,
                     name = "URL",
                     description = "URL of a repository containing artifacts") 
            final String urlStr,
            @Option(required = true,
                    name = "name",
                    description = "Name of the artifact module") 
            final String name,
            @Option(required = false,
                     name = "download",
                     description = "Set to true to download the artifact from the repository.") 
            final Boolean download,
            final PipeOut out) {
        

        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);
        
        URL url;
        try {
            url = new URL(urlStr);
        } catch (Exception ex) {
            out.println(out.renderColor(ShellColor.RED, "Invalid Artifact URL: " + urlStr));
            out.println(out.renderColor(ShellColor.RED, ex.toString()));
            return;
        }
        
        // Download the artifact if requested
        if (download == null || download) {
            try {
                File artifactDir = new File(ARTIFACT_DIR);
                if (!artifactDir.exists()) {
                    artifactDir.mkdirs();
                }
                // detect if this is a Guvnor repository
                if (url.getProtocol().contains("http") && url.getPath().contains("rest/packages")) {
                    url = new URL(url.toString() + "/binary");
                }
                File artifactFile = new File(artifactDir, name + ".jar");
                streamToFile(url.openStream(), artifactFile);
                
            } catch (Exception ex) {
                out.println(out.renderColor(ShellColor.RED, "Invalid Artifact URL: " + urlStr));
                out.println(out.renderColor(ShellColor.RED, ex.toString()));
                return;
            }
        }
        
        // update config
        ArtifactsModel artifacts = switchYard.getSwitchYardConfig().getArtifacts();
        if (artifacts == null) {
            artifacts = new V1ArtifactsModel();
            switchYard.getSwitchYardConfig().setArtifacts(artifacts);
        }
        ArtifactModel artifact = new V1ArtifactModel();
        artifact.setName(name);
        artifact.setURL(urlStr);
        artifacts.addArtifact(artifact);
        switchYard.saveConfig();
    }
    
    // This method reads from the source stream and writes to the specified path.
    // Both the input and output streams are closed by this method.
    private void streamToFile(InputStream stream, File filePath) throws Exception {
        if (filePath.exists()) {
            throw new Exception("File already exists: " + filePath);
        }

        FileOutputStream fos = null;
        try { 
            fos = new FileOutputStream(filePath);
            int count;
            byte[] buf = new byte[8192];
            while ((count = stream.read(buf)) != -1) {
                fos.write(buf, 0, count);
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (stream != null) {
                stream.close();
            }
        }
    }



    /**
     * Add a component-level reference to a given service.
     * @param referenceName the name of the reference being created
     * @param interfaceType possible values: wsdl, java
     * @param interfaze The interface of the reference
     * @param componentName the name of the component the reference will be applied to 
     * @param out shell output
     */
    @Command(value = "add-reference", help = "Add a component-level reference to a given service.")
    public void addReference(
            @Option(required = true,
                    name = "referenceName",
                    description = "The name of the reference being created") final String referenceName,
            @Option(required = true,
                    name = "interfaceType",
                    description = "Possible values: wsdl, java") final String interfaceType,
            @Option(required = true,
                    name = "interface",
                    description = "The interface of the reference") final String interfaze,
            @Option(required = true,
                    name = "componentName",
                    description = "The name of the component where the reference will be added") final String componentName,
                    final PipeOut out) {

        SwitchYardFacet switchYard = _project.getFacet(SwitchYardFacet.class);

        // Make sure the source component service exists
        ComponentServiceModel sourceComponent = switchYard.getComponentService(componentName);
        if (sourceComponent == null) {
            out.println(out.renderColor(ShellColor.RED, "Component not found: " + componentName));
            return;
        } else {
            // Check the reference name is not already present in this component
            Iterator<ComponentReferenceModel> references = sourceComponent.getComponent().getReferences().iterator();
            while (references.hasNext()) {
                ComponentReferenceModel reference = references.next();
                if (reference.getName().equals(referenceName)) {
                    out.println(out.renderColor(ShellColor.RED, "A reference named " + referenceName + " already exists in " + componentName));
                    return;
                }
            }
        }

        // Make sure the interface type is valid
        if (!interfaceType.equals("java") || interfaceType.equals("wsdl")) {
            out.println(out.renderColor(ShellColor.RED, "Interface type " + interfaceType + " not valid. Possible values are: wsdl, java"));
            return;
        }
        
        addComponentReference(switchYard, componentName, referenceName, interfaceType, interfaze, out);
        
        //Notify user of success
        out.println("Reference " + referenceName + " successfully added to component " + componentName);
        
    }

    private void addComponentReference(SwitchYardFacet switchYard, String componentName, String referenceName, String interfaceType, String interfaze, PipeOut out) {

        ComponentReferenceModel reference = new V1ComponentReferenceModel();
        reference.setName(referenceName);
        V1InterfaceModel referenceInterfaceModel = new V1InterfaceModel(interfaceType);
        referenceInterfaceModel.setInterface(interfaze);
        reference.setInterface(referenceInterfaceModel);
        
        SwitchYardModel userConfig = switchYard.getSwitchYardConfig();
        boolean isComponentInUserConfig = false;
        for (Iterator<ComponentModel> userConfigComponents = userConfig.getComposite().getComponents().iterator(); userConfigComponents.hasNext();) {
            ComponentModel componentModel = userConfigComponents.next();
            if (componentModel.getName().equals(componentName)) {
                
                //The component is already in the user config. Let's just add the reference to it
                componentModel.addReference(reference);
                isComponentInUserConfig = true;
                break;
            }
        }
        
        if (!isComponentInUserConfig) {
            //The component is not in the user config. Let's: 1) get it from the merged config, 2) add the reference into it, and 
            //finally 3) save the component into the userConfig 
            SwitchYardModel mergedConfig = switchYard.getMergedSwitchYardConfig();
            for (ComponentModel componentModel : mergedConfig.getComposite().getComponents()) {
                if (componentModel.getName().equals(componentName)) {
                    componentModel.addReference(reference);
                    userConfig.getComposite().addComponent(componentModel);
                    break;
                }
            }
        }

        switchYard.saveConfig();
    }
    
}
