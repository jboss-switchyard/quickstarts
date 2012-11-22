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
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIBE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REMOVE;
import static org.switchyard.as7.extension.CommonAttributes.IDENTIFIER;
import static org.switchyard.as7.extension.CommonAttributes.IMPLCLASS;
import static org.switchyard.as7.extension.CommonAttributes.MODULE;
import static org.switchyard.as7.extension.CommonAttributes.PROPERTIES;
import static org.switchyard.as7.extension.CommonAttributes.SOCKET_BINDING;

import java.util.Locale;

import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ReloadRequiredWriteAttributeHandler;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.operations.validation.ModelTypeValidator;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.OperationEntry;
import org.jboss.as.controller.registry.AttributeAccess.Storage;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.logging.Logger;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemGetVersion;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemListApplications;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemListComponents;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemListServices;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemReadApplication;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemReadComponent;
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

    /** {@inheritDoc} */
    @Override
    public void initialize(final ExtensionContext context) {
        // log SwitchYard notification message (includes version information)
        LOGGER.info(Versions.getSwitchYardNotification());

        final SubsystemRegistration subsystem = context.registerSubsystem(SUBSYSTEM_NAME, 1, 0);
        final ManagementResourceRegistration registration = subsystem.registerSubsystemModel(SwitchYardSubsystemProviders.SUBSYSTEM_DESCRIBE);
        registration.registerOperationHandler(ADD, SwitchYardSubsystemAdd.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_ADD_DESCRIBE, false);
        registration.registerOperationHandler(REMOVE, SwitchYardSubsystemRemove.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_REMOVE_DESCRIBE, false);
        registration.registerOperationHandler(DESCRIBE, SwitchYardSubsystemDescribe.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_DESCRIBE, false, OperationEntry.EntryType.PRIVATE);
        registration.registerReadWriteAttribute(SOCKET_BINDING, null, new ReloadRequiredWriteAttributeHandler(new ModelTypeValidator(ModelType.STRING)), Storage.CONFIGURATION);
        registration.registerReadWriteAttribute(PROPERTIES, null, new ReloadRequiredWriteAttributeHandler(new ModelTypeValidator(ModelType.STRING)), Storage.CONFIGURATION);
        subsystem.registerXMLElementWriter(SwitchYardSubsystemWriter.getInstance());

        // SwitchYard modules
        final ManagementResourceRegistration modules = registration.registerSubModel(PathElement.pathElement(MODULE), SwitchYardSubsystemProviders.MODULE_DESCRIBE);
        modules.registerOperationHandler(ADD, SwitchYardModuleAdd.INSTANCE, SwitchYardSubsystemProviders.MODULE_ADD_DESCRIBE, false);
        modules.registerOperationHandler(REMOVE, SwitchYardModuleRemove.INSTANCE, SwitchYardSubsystemProviders.MODULE_REMOVE_DESCRIBE, false);
        modules.registerReadWriteAttribute(IDENTIFIER, null, new ReloadRequiredWriteAttributeHandler(new ModelTypeValidator(ModelType.STRING)), Storage.CONFIGURATION);
        modules.registerReadWriteAttribute(IMPLCLASS, null, new ReloadRequiredWriteAttributeHandler(new ModelTypeValidator(ModelType.STRING)), Storage.CONFIGURATION);
        modules.registerReadWriteAttribute(PROPERTIES, null, new ReloadRequiredWriteAttributeHandler(new ModelTypeValidator(ModelType.STRING)), Storage.CONFIGURATION);

        // register administrative functions
        registration.registerOperationHandler(SwitchYardModelConstants.GET_VERSION, SwitchYardSubsystemGetVersion.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_GET_VERSION, false);
        registration.registerOperationHandler(SwitchYardModelConstants.LIST_APPLICATIONS, SwitchYardSubsystemListApplications.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_LIST_APPLICATIONS, false);
        registration.registerOperationHandler(SwitchYardModelConstants.LIST_COMPONENTS, SwitchYardSubsystemListComponents.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_LIST_COMPONENTS, false);
        registration.registerOperationHandler(SwitchYardModelConstants.LIST_SERVICES, SwitchYardSubsystemListServices.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_LIST_SERVICES, false);
        registration.registerOperationHandler(SwitchYardModelConstants.READ_APPLICATION, SwitchYardSubsystemReadApplication.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_READ_APPLICATION, false);
        registration.registerOperationHandler(SwitchYardModelConstants.READ_COMPONENT, SwitchYardSubsystemReadComponent.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_READ_COMPONENT, false);
        registration.registerOperationHandler(SwitchYardModelConstants.READ_SERVICE, SwitchYardSubsystemReadService.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_READ_SERVICE, false);
        registration.registerOperationHandler(SwitchYardModelConstants.USES_ARTIFACT, SwitchYardSubsystemUsesArtifact.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_USES_ARTIFACT, false);
        registration.registerOperationHandler(SwitchYardModelConstants.SHOW_METRICS, SwitchYardSubsystemShowMetrics.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_SHOW_METRICS, false);

        DescriptionProvider nullDescriptionProvider = new DescriptionProvider() {
            @Override
            public ModelNode getModelDescription(Locale locale) {
                return new ModelNode();
            }
        };
        final ManagementResourceRegistration deployments = subsystem.registerDeploymentModel(nullDescriptionProvider);
        deployments.registerSubModel(PathElement.pathElement(SwitchYardModelConstants.APPLICATION),
                nullDescriptionProvider);
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
