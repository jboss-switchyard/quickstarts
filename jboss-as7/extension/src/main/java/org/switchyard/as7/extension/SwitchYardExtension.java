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
import static org.switchyard.as7.extension.CommonAttributes.MODULE;

import java.util.Locale;

import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.OperationDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ReloadRequiredWriteAttributeHandler;
import org.jboss.as.controller.ResourceBuilder;
import org.jboss.as.controller.ResourceDefinition;
import org.jboss.as.controller.SimpleOperationDefinitionBuilder;
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
import org.switchyard.as7.extension.admin.SwitchYardSubsystemListServices;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemReadApplication;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemReadService;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemShowMetrics;
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
    private static final String RESOURCE_NAME = SwitchYardExtension.class.getPackage().getName() + ".LocalDescriptions";

    private static final OperationDefinition GET_VERSION = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.GET_VERSION, getResourceDescriptionResolver("switchyard.get-version"))
            .build();
    private static final OperationDefinition LIST_APPLICATIONS = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.LIST_APPLICATIONS, getResourceDescriptionResolver("switchyard.list-applications"))
            .build();
    private static final OperationDefinition LIST_SERVICES = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.LIST_SERVICES, getResourceDescriptionResolver("switchyard.list-services"))
            .build();
    private static final OperationDefinition READ_APPLICATION = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.READ_APPLICATION, getResourceDescriptionResolver("switchyard.read-application"))
            .build();
    private static final OperationDefinition READ_SERVICE = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.READ_SERVICE, getResourceDescriptionResolver("switchyard.read-service"))
            .build();
    private static final OperationDefinition USES_ARTIFACT = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.USES_ARTIFACT, getResourceDescriptionResolver("switchyard.uses-artifacts"))
            .build();
    private static final OperationDefinition SHOW_METRICS = new SimpleOperationDefinitionBuilder(SwitchYardModelConstants.SHOW_METRICS, getResourceDescriptionResolver("switchyard.show-metrics"))
            .build();

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

        ResourceDefinition subsystemResource = ResourceBuilder.Factory.createSubsystemRoot(SUBSYSTEM_PATH, getResourceDescriptionResolver(), SwitchYardSubsystemAdd.INSTANCE, SwitchYardSubsystemRemove.INSTANCE)
                .addReadWriteAttribute(Attributes.SOCKET_BINDING, null, new ReloadRequiredWriteAttributeHandler(Attributes.SOCKET_BINDING))
                .addReadWriteAttribute(Attributes.PROPERTIES, null, new ReloadRequiredWriteAttributeHandler(Attributes.PROPERTIES))
                .addOperation(GET_VERSION, SwitchYardSubsystemGetVersion.INSTANCE)
                .addOperation(LIST_APPLICATIONS, SwitchYardSubsystemListApplications.INSTANCE)
                .addOperation(LIST_SERVICES, SwitchYardSubsystemListServices.INSTANCE)
                .addOperation(READ_APPLICATION, SwitchYardSubsystemReadApplication.INSTANCE)
                .addOperation(READ_SERVICE, SwitchYardSubsystemReadService.INSTANCE)
                .addOperation(USES_ARTIFACT, SwitchYardSubsystemUsesArtifact.INSTANCE)
                .addOperation(SHOW_METRICS, SwitchYardSubsystemShowMetrics.INSTANCE)
                .pushChild(modulesResource).pop()
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
