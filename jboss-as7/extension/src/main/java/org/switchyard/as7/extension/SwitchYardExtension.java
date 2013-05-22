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
package org.switchyard.as7.extension;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;
import static org.switchyard.as7.extension.CommonAttributes.EXTENSION;
import static org.switchyard.as7.extension.CommonAttributes.MODULE;

import java.util.Locale;

import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ReloadRequiredWriteAttributeHandler;
import org.jboss.as.controller.ResourceBuilder;
import org.jboss.as.controller.ResourceDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.descriptions.StandardResourceDescriptionResolver;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemGetVersion;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemListApplications;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemListReferences;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemListServices;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemReadApplication;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemReadReference;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemReadService;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemResetMetrics;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemShowMetrics;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemStartGateway;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemStopGateway;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemUsesArtifact;
import org.switchyard.common.version.Versions;

/**
 * Domain extension used to initialize the SwitchYard subsystem.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger("org.switchyard");

    /** The subsystem name. */
    public static final String SUBSYSTEM_NAME = "switchyard";

    /** Namespace for this subsystem. */
    public static final String NAMESPACE = "urn:jboss:domain:switchyard:1.0";

    private static final PathElement SUBSYSTEM_PATH = PathElement.pathElement(SUBSYSTEM, SUBSYSTEM_NAME);
    private static final PathElement MODULE_PATH = PathElement.pathElement(MODULE);
    private static final PathElement EXTENSION_PATH = PathElement.pathElement(EXTENSION);
    private static final String RESOURCE_NAME = SwitchYardExtension.class.getPackage().getName() + ".LocalDescriptions";

    /**
     * Create description resolver.
     * @param keyPrefix a list of prefixes
     * @return the decscription resolver
     */
    public static StandardResourceDescriptionResolver getResourceDescriptionResolver(final String... keyPrefix) {
        StringBuilder prefix = new StringBuilder(SUBSYSTEM_NAME);
        for (String kp : keyPrefix) {
            prefix.append('.').append(kp);
        }
        return new StandardResourceDescriptionResolver(prefix.toString(), RESOURCE_NAME, SwitchYardExtension.class.getClassLoader(), true, false);
    }


    /** {@inheritDoc} */
    @Override
    public void initialize(final ExtensionContext context) {
        // log SwitchYard notification message (includes version information)
        LOGGER.info(Versions.getSwitchYardNotification());

        final SubsystemRegistration subsystem = context.registerSubsystem(SUBSYSTEM_NAME, 1, 0);
        subsystem.registerXMLElementWriter(SwitchYardSubsystemWriter.getInstance());

        ResourceBuilder modulesResource = ResourceBuilder.Factory.create(MODULE_PATH, getResourceDescriptionResolver(MODULE))
                .setAddOperation(SwitchYardModuleAdd.INSTANCE)
                .setRemoveOperation(SwitchYardModuleRemove.INSTANCE)
                .addReadWriteAttribute(Attributes.IDENTIFIER, null, new ReloadRequiredWriteAttributeHandler(Attributes.IDENTIFIER))
                .addReadWriteAttribute(Attributes.IMPLCLASS, null, new ReloadRequiredWriteAttributeHandler(Attributes.IMPLCLASS))
                .addReadWriteAttribute(Attributes.PROPERTIES, null, new ReloadRequiredWriteAttributeHandler(Attributes.PROPERTIES));

        ResourceBuilder extensionsResource = ResourceBuilder.Factory.create(EXTENSION_PATH, getResourceDescriptionResolver(EXTENSION))
                .setAddOperation(SwitchYardExtensionAdd.INSTANCE)
                .setRemoveOperation(SwitchYardExtensionRemove.INSTANCE)
                .addReadWriteAttribute(Attributes.IDENTIFIER, null, new ReloadRequiredWriteAttributeHandler(Attributes.IDENTIFIER));

        ResourceDefinition subsystemResource = ResourceBuilder.Factory.createSubsystemRoot(SUBSYSTEM_PATH, getResourceDescriptionResolver(), SwitchYardSubsystemAdd.INSTANCE, SwitchYardSubsystemRemove.INSTANCE)
                .addReadWriteAttribute(Attributes.SOCKET_BINDING, null, new ReloadRequiredWriteAttributeHandler(Attributes.SOCKET_BINDING))
                .addReadWriteAttribute(Attributes.PROPERTIES, null, new ReloadRequiredWriteAttributeHandler(Attributes.PROPERTIES))
                .addOperation(Operations.GET_VERSION, SwitchYardSubsystemGetVersion.INSTANCE)
                .addOperation(Operations.LIST_APPLICATIONS, SwitchYardSubsystemListApplications.INSTANCE)
                .addOperation(Operations.LIST_REFERENCES, SwitchYardSubsystemListReferences.INSTANCE)
                .addOperation(Operations.LIST_SERVICES, SwitchYardSubsystemListServices.INSTANCE)
                .addOperation(Operations.READ_APPLICATION, SwitchYardSubsystemReadApplication.INSTANCE)
                .addOperation(Operations.READ_REFERENCE, SwitchYardSubsystemReadReference.INSTANCE)
                .addOperation(Operations.READ_SERVICE, SwitchYardSubsystemReadService.INSTANCE)
                .addOperation(Operations.USES_ARTIFACT, SwitchYardSubsystemUsesArtifact.INSTANCE)
                .addOperation(Operations.SHOW_METRICS, SwitchYardSubsystemShowMetrics.INSTANCE)
                .addOperation(Operations.RESET_METRICS, SwitchYardSubsystemResetMetrics.INSTANCE)
                .addOperation(Operations.STOP_GATEWAY, SwitchYardSubsystemStopGateway.INSTANCE)
                .addOperation(Operations.START_GATEWAY, SwitchYardSubsystemStartGateway.INSTANCE)
                .pushChild(modulesResource).pop()
                .pushChild(extensionsResource).pop()
                .build();
        subsystem.registerSubsystemModel(subsystemResource);

        DescriptionProvider nullDescriptionProvider = new DescriptionProvider() {
            @Override
            public ModelNode getModelDescription(Locale locale) {
                return new ModelNode();
            }
        };

        final ManagementResourceRegistration registration = subsystem.registerDeploymentModel(new SimpleResourceDefinition(SUBSYSTEM_PATH, getResourceDescriptionResolver("deployment")));
        registration.registerSubModel(new SimpleResourceDefinition(PathElement.pathElement(SwitchYardModelConstants.APPLICATION), getResourceDescriptionResolver()));
    }

    /** {@inheritDoc} */
    @Override
    public void initializeParsers(final ExtensionParsingContext context) {
        context.setSubsystemXmlMapping(SUBSYSTEM_NAME, SwitchYardExtension.NAMESPACE, SwitchYardSubsystemReader.getInstance());
    }

    /**
     * Create an Add subsystem operation.
     * 
     * @return the operation node
     */
    public static ModelNode createAddSubsystemOperation() {
        final ModelNode subsystem = new ModelNode();
        subsystem.get(OP).set(ADD);
        subsystem.get(OP_ADDR).add(ModelDescriptionConstants.SUBSYSTEM, SUBSYSTEM_NAME);
        return subsystem;
    }
}
