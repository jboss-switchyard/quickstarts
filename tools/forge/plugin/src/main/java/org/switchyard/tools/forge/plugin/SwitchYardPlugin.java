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
import org.switchyard.config.model.composite.v1.V1CompositeReferenceModel;
import org.switchyard.config.model.composite.v1.V1CompositeServiceModel;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.HandlerModel;
import org.switchyard.config.model.domain.HandlersModel;
import org.switchyard.config.model.domain.v1.V1DomainModel;
import org.switchyard.config.model.domain.v1.V1HandlerModel;
import org.switchyard.config.model.domain.v1.V1HandlersModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Project-level commands for SwitchYard applications.
 */
@Alias("switchyard")
@RequiresProject
@Topic("SOA")
@RequiresFacet(SwitchYardFacet.class)
@Help("Plugin for creating service-oriented applications with SwitchYard.")
public class SwitchYardPlugin implements Plugin {
    
    // Template file used for unit testing services
    private static final String TEST_SERVICE_TEMPLATE = "java/TestTemplate.java";
    // MessageTrace handler name and class
    private static final String TRACE_CLASS = "org.switchyard.handlers.MessageTrace";
    private static final String TRACE_NAME = "MessageTrace";

    @Inject
    private Project _project;

    @Inject
    private Shell _shell;

    /**
     * List SwitchYard services available in the project.
     * @param out shell output
     */
    @Command(value = "show-config", help = "Show the current configuration state of the application.")
    public void listServices(final PipeOut out) {
        SwitchYardModel config = _project.getFacet(SwitchYardFacet.class).getMergedSwitchYardConfig();
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
}
