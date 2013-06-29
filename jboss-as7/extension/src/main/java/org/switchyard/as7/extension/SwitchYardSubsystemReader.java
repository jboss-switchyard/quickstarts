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
package org.switchyard.as7.extension;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.as.controller.parsing.ParseUtils.missingRequired;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoAttributes;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoContent;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoNamespaceAttribute;
import static org.jboss.as.controller.parsing.ParseUtils.unexpectedAttribute;
import static org.jboss.as.controller.parsing.ParseUtils.unexpectedElement;
import static org.switchyard.as7.extension.CommonAttributes.IDENTIFIER;
import static org.switchyard.as7.extension.CommonAttributes.IMPLCLASS;
import static org.switchyard.as7.extension.CommonAttributes.MODULE;
import static org.switchyard.as7.extension.CommonAttributes.MODULES;
import static org.switchyard.as7.extension.CommonAttributes.EXTENSION;
import static org.switchyard.as7.extension.CommonAttributes.PROPERTIES;
import static org.switchyard.as7.extension.CommonAttributes.SOCKET_BINDING;

import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;

/**
 * A SwitchYard subsystem reader/parser.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
final class SwitchYardSubsystemReader implements XMLStreamConstants, XMLElementReader<List<ModelNode>> {

    private static final SwitchYardSubsystemReader INSTANCE = new SwitchYardSubsystemReader();

    private SwitchYardSubsystemReader() {
        // forbidden instantiation
    }

    static SwitchYardSubsystemReader getInstance() {
        return INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    public void readElement(final XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {
        // Require no attributes
        requireNoAttributes(reader);
        // Add our subsystem's 'add' operation
        ModelNode subsystem = SwitchYardExtension.createAddSubsystemOperation();
        list.add(subsystem);
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
                            subsystem.get(SOCKET_BINDING).set(sockets);
                        }
                        requireNoContent(reader);
                        break;
                    case MODULES:
                        parseModulesElement(reader, list);
                        break;
                    case EXTENSIONS:
                        parseExtensionsElement(reader, list);
                        break;
                    case PROPERTIES:
                        ModelNode properties = parsePropertiesElement("", reader);
                        if (properties != null) {
                            subsystem.get(PROPERTIES).set(properties);
                        }
                        break;
                    default:
                        throw unexpectedElement(reader);
                }
            }
        }
    }

    void parseModulesElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {

        // Handle attributes
        requireNoAttributes(reader);

        // Handle module elements
        while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
            if (reader.getNamespaceURI().equals(SwitchYardExtension.NAMESPACE)) {
                final Element element = Element.forName(reader.getLocalName());
                if (element == Element.MODULE) {
                    parseModuleElement(reader, list);
                } else {
                    throw unexpectedElement(reader);
                }
            }
        }
    }

    void parseModuleElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {
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

        //Add the 'add' operation for each 'module' child
        ModelNode moduleAdd = new ModelNode();
        moduleAdd.get(OP).set(ModelDescriptionConstants.ADD);
        PathAddress addr = PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, SwitchYardExtension.SUBSYSTEM_NAME), PathElement.pathElement(MODULE, identifier));
        moduleAdd.get(OP_ADDR).set(addr.toModelNode());
        moduleAdd.get(IMPLCLASS).set(implClass);

        while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
            final Element element1 = Element.forName(reader.getLocalName());
            switch (element1) {
                case PROPERTIES: 
                    ModelNode properties = parsePropertiesElement(identifier, reader);
                    if (properties != null) {
                        moduleAdd.get(PROPERTIES).set(properties);
                    }
                    break;
                default:
                    throw unexpectedElement(reader);
            }
        }

        list.add(moduleAdd);
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


    void parseExtensionsElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {

        // Handle attributes
        requireNoAttributes(reader);

        // Handle module elements
        while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
            if (reader.getNamespaceURI().equals(SwitchYardExtension.NAMESPACE)) {
                final Element element = Element.forName(reader.getLocalName());
                if (element == Element.EXTENSION) {
                    parseExtensionElement(reader, list);
                } else {
                    throw unexpectedElement(reader);
                }
            }
        }
    }

    void parseExtensionElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {
        String identifier = null;
        final int count = reader.getAttributeCount();
        for (int i = 0; i < count; i++) {
            requireNoNamespaceAttribute(reader, i);
            final Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
            switch (attribute) {
                case IDENTIFIER:
                    identifier = reader.getAttributeValue(i);
                    break;
                default:
                    throw unexpectedAttribute(reader, i);
            }
        }
        if (identifier == null) {
            throw missingRequired(reader, Collections.singleton(Attribute.IDENTIFIER));
        }

        //Add the 'add' operation for each 'module' child
        ModelNode moduleAdd = new ModelNode();
        moduleAdd.get(OP).set(ModelDescriptionConstants.ADD);
        PathAddress addr = PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, SwitchYardExtension.SUBSYSTEM_NAME), PathElement.pathElement(EXTENSION, identifier));
        moduleAdd.get(OP_ADDR).set(addr.toModelNode());

        while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
            throw unexpectedElement(reader);
        }

        list.add(moduleAdd);
    }

}
