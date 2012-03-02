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
import static org.jboss.as.controller.parsing.ParseUtils.missingRequired;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoAttributes;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoContent;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoNamespaceAttribute;
import static org.jboss.as.controller.parsing.ParseUtils.unexpectedAttribute;
import static org.jboss.as.controller.parsing.ParseUtils.unexpectedElement;
import static org.switchyard.as7.extension.CommonAttributes.IMPLCLASS;
import static org.switchyard.as7.extension.CommonAttributes.MODULES;
import static org.switchyard.as7.extension.CommonAttributes.PROPERTIES;
import static org.switchyard.as7.extension.CommonAttributes.SOCKET_BINDING;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.OperationEntry;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemGetVersion;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemListApplications;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemListComponents;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemListServices;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemReadApplication;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemReadComponent;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemReadService;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemShowMetrics;
import org.switchyard.as7.extension.admin.SwitchYardSubsystemUsesArtifact;

/**
 * Domain extension used to initialize the SwitchYard subsystem.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardExtension implements Extension {

    /** The subsystem name. */
    public static final String SUBSYSTEM_NAME = "switchyard";

    /** Namespace for this subsystem. */
    public static final String NAMESPACE = "urn:jboss:domain:switchyard:1.0";

    private static final SwitchYardSubsystemParser PARSER = new SwitchYardSubsystemParser();

    /** {@inheritDoc} */
    @Override
    public void initialize(final ExtensionContext context) {
        final SubsystemRegistration subsystem = context.registerSubsystem(SUBSYSTEM_NAME);
        final ManagementResourceRegistration registration = subsystem.registerSubsystemModel(SwitchYardSubsystemProviders.SUBSYSTEM_DESCRIBE);
        registration.registerOperationHandler(ADD, SwitchYardSubsystemAdd.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_ADD, false);
        registration.registerOperationHandler(DESCRIBE, SwitchYardSubsystemDescribe.INSTANCE, SwitchYardSubsystemProviders.SUBSYSTEM_DESCRIBE, false, OperationEntry.EntryType.PRIVATE);
        subsystem.registerXMLElementWriter(PARSER);

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
        context.setSubsystemXmlMapping(SUBSYSTEM_NAME, SwitchYardExtension.NAMESPACE, PARSER);
    }

    private static ModelNode createAddSubSystemOperation() {
        final ModelNode subsystem = new ModelNode();
        subsystem.get(OP).set(ADD);
        subsystem.get(OP_ADDR).add(ModelDescriptionConstants.SUBSYSTEM, SUBSYSTEM_NAME);
        return subsystem;
    }

    static class SwitchYardSubsystemParser implements XMLStreamConstants, XMLElementReader<List<ModelNode>>, XMLElementWriter<SubsystemMarshallingContext> {

        /** {@inheritDoc} */
        @Override
        public void readElement(final XMLExtendedStreamReader reader, final List<ModelNode> list) throws XMLStreamException {
            // Require no attributes
            requireNoAttributes(reader);
            ModelNode subsytem = createAddSubSystemOperation();
            // Elements
            while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
                if (reader.getNamespaceURI().equals(SwitchYardExtension.NAMESPACE)) {
                    final Element element = Element.forName(reader.getLocalName());
                    switch (element) {
                        case SOCKET_BINDING: 
                            String sockets = null;
                            final int count = reader.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                requireNoNamespaceAttribute(reader, i);
                                final Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
                                switch (attribute) {
                                    case NAMES:
                                        sockets = reader.getAttributeValue(i);
                                        break;
                                    default:
                                        throw unexpectedAttribute(reader, i);
                                }
                            }
                            if (sockets != null) {
                                subsytem.get(SOCKET_BINDING).set(sockets);
                            }
                            requireNoContent(reader);
                            break;
                        case MODULES: 
                            ModelNode modules = parseModulesElement(reader);
                            if (modules != null) {
                                subsytem.get(MODULES).set(modules);
                            }
                            break;
                        case PROPERTIES: 
                            ModelNode properties = parsePropertiesElement("", reader);
                            if (properties != null) {
                                subsytem.get(PROPERTIES).set(properties);
                            }
                            break;
                        default:
                            throw unexpectedElement(reader);
                    }
                }
            }

            list.add(subsytem);
        }

        /** {@inheritDoc} */
        @Override
        public void writeContent(final XMLExtendedStreamWriter writer, final SubsystemMarshallingContext context) throws XMLStreamException {
            context.startSubsystemElement(SwitchYardExtension.NAMESPACE, false);
            ModelNode node = context.getModelNode();
            if (has(node, SOCKET_BINDING)) {
                ModelNode socketNames = node.get(SOCKET_BINDING);
                writer.writeEmptyElement(Element.SOCKET_BINDING.getLocalName());
                writer.writeAttribute(Attribute.NAMES.getLocalName(), socketNames.asString());
            }
            if (has(node, MODULES)) {
                ModelNode modules = node.get(MODULES);
                writer.writeStartElement(Element.MODULES.getLocalName());
                Set<String> keys = modules.keys();
                for (String current : keys) {
                    writer.writeStartElement(Element.MODULE.getLocalName());
                    writer.writeAttribute(Attribute.IDENTIFIER.getLocalName(), current);
                    writer.writeAttribute(Attribute.IMPLCLASS.getLocalName(), modules.get(current).get(IMPLCLASS).asString());
                    writeProperties(modules.get(current), writer);
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }
            writeProperties(node, writer);
            writer.writeEndElement();
        }

        public void writeProperties(final ModelNode node, final XMLExtendedStreamWriter writer) throws XMLStreamException {
            if (has(node, PROPERTIES)) {
                ModelNode properties = node.get(PROPERTIES);
                writer.writeStartElement(Element.PROPERTIES.getLocalName());
                Set<String> keys = properties.keys();
                for (String current : keys) {
                    writer.writeStartElement(current);
                    writer.writeCharacters(properties.get(current).asString());
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }
        }

        private boolean has(ModelNode node, String name) {
            return node.has(name) && node.get(name).isDefined();
        }

        ModelNode parseModulesElement(XMLExtendedStreamReader reader) throws XMLStreamException {

            // Handle attributes
            requireNoAttributes(reader);

            ModelNode modules = null;

            // Handle elements
            while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
                if (reader.getNamespaceURI().equals(SwitchYardExtension.NAMESPACE)) {
                    final Element element = Element.forName(reader.getLocalName());
                    if (element == Element.MODULE) {
                        if (modules == null) {
                            modules = new ModelNode();
                        }
                        String identifier = null;
                        String implClass = null;
                        final int count = reader.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            requireNoNamespaceAttribute(reader, i);
                            final Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
                            switch (attribute) {
                                case IDENTIFIER:
                                    identifier = reader.getAttributeValue(i);
                                    break;
                                case IMPLCLASS:
                                    implClass = reader.getAttributeValue(i);
                                    break;
                                default:
                                    throw unexpectedAttribute(reader, i);
                            }
                        }
                        if (identifier == null) {
                            throw missingRequired(reader, Collections.singleton(Attribute.IDENTIFIER));
                        }
                        if (implClass == null) {
                            throw missingRequired(reader, Collections.singleton(Attribute.IMPLCLASS));
                        }
                        if (modules.has(identifier)) {
                            throw new XMLStreamException(element.getLocalName() + " already declared", reader.getLocation());
                        }

                        ModelNode module = new ModelNode();
                        module.get(IMPLCLASS).set(implClass);
                        while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
                            final Element element1 = Element.forName(reader.getLocalName());
                            switch (element1) {
                                case PROPERTIES: 
                                    ModelNode properties = parsePropertiesElement(identifier, reader);
                                    if (properties != null) {
                                        module.get(PROPERTIES).set(properties);
                                    }
                                    break;
                                default:
                                    throw unexpectedElement(reader);
                            }
                        }
                        modules.get(identifier).set(module);
                    } else {
                        throw unexpectedElement(reader);
                    }
                }
            }

            return modules;
        }

        ModelNode parsePropertiesElement(String identifier, XMLExtendedStreamReader reader) throws XMLStreamException {

            // Handle attributes
            requireNoAttributes(reader);

            ModelNode properties = new ModelNode();
            StringBuffer configModel = new StringBuffer();

            // Handle elements
            while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
                if (reader.getNamespaceURI().equals(SwitchYardExtension.NAMESPACE)) {
                    final Element element = Element.forName(reader.getLocalName());
                    String name = reader.getLocalName();
                    String value = reader.getElementText();
                    if (properties.has(name)) {
                        throw new XMLStreamException(element.getLocalName() + " already declared", reader.getLocation());
                    }

                    ModelNode property = new ModelNode();
                    property.set(value);
                    properties.get(name).set(property);
                    configModel.append(element.toString());
                }
            }

            return properties;
        }
    }

}
