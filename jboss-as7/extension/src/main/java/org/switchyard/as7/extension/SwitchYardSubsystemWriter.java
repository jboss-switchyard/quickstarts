/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
