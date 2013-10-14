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

import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * A SwitchYard subsystem writer.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
final class SwitchYardSubsystemWriter implements XMLElementWriter<SubsystemMarshallingContext> {

    private static final SwitchYardSubsystemWriter INSTANCE = new SwitchYardSubsystemWriter();

    private SwitchYardSubsystemWriter() {
        // forbidden instantiation
    }

    static SwitchYardSubsystemWriter getInstance() {
        return INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    public void writeContent(final XMLExtendedStreamWriter writer, final SubsystemMarshallingContext context) throws XMLStreamException {
        context.startSubsystemElement(SwitchYardExtension.NAMESPACE, false);
        ModelNode node = context.getModelNode();
        if (node.hasDefined(CommonAttributes.SOCKET_BINDING)) {
            ModelNode socketNames = node.get(CommonAttributes.SOCKET_BINDING);
            writer.writeEmptyElement(Element.SOCKET_BINDING.getLocalName());
            writer.writeAttribute(Attribute.NAMES.getLocalName(), socketNames.asString());
        }
        if (node.hasDefined(CommonAttributes.SECURITY_CONFIG)) {
            ModelNode modules = node.get(CommonAttributes.SECURITY_CONFIG);
            writer.writeStartElement(Element.SECURITY_CONFIGS.getLocalName());
            for (Property property : modules.asPropertyList()) {
                writer.writeStartElement(Element.SECURITY_CONFIG.getLocalName());
                writer.writeAttribute(Attribute.IDENTIFIER.getLocalName(), property.getName());
                ModelNode entry = property.getValue();
                writeProperties(entry, writer);
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }
        if (node.hasDefined(CommonAttributes.MODULE)) {
            ModelNode modules = node.get(CommonAttributes.MODULE);
            writer.writeStartElement(Element.MODULES.getLocalName());
            for (Property property : modules.asPropertyList()) {
                writer.writeStartElement(Element.MODULE.getLocalName());
                writer.writeAttribute(Attribute.IDENTIFIER.getLocalName(), property.getName());
                ModelNode entry = property.getValue();
                if (entry.hasDefined(CommonAttributes.IMPLCLASS)) {
                    writer.writeAttribute(Attribute.IMPLCLASS.getLocalName(), entry.get(CommonAttributes.IMPLCLASS).asString());
                }
                writeProperties(entry, writer);
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }
        if (node.hasDefined(CommonAttributes.EXTENSION)) {
            ModelNode modules = node.get(CommonAttributes.EXTENSION);
            writer.writeStartElement(Element.EXTENSIONS.getLocalName());
            for (Property property : modules.asPropertyList()) {
                writer.writeStartElement(Element.EXTENSION.getLocalName());
                writer.writeAttribute(Attribute.IDENTIFIER.getLocalName(), property.getName());
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }
        writeProperties(node, writer);
        writer.writeEndElement();
    }

    private static void writeProperties(final ModelNode node, final XMLExtendedStreamWriter writer) throws XMLStreamException {
        if (node.hasDefined(CommonAttributes.PROPERTIES)) {
            ModelNode properties = node.get(CommonAttributes.PROPERTIES);
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
}
