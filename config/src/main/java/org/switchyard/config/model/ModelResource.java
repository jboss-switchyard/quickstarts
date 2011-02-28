/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.config.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.namespace.QName;

import org.switchyard.config.ConfigurationResource;
import org.switchyard.config.util.ElementResource;
import org.switchyard.config.util.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Utility class to safely access ("pull") Models from various sources.
 *
 * @param <M> the Model type
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ModelResource<M extends Model> extends Resource<M> {

    private Descriptor _desc;

    /**
     * Constructs a default ModelResource with a default Descriptor.
     */
    public ModelResource() {
        this(null);
    }

    /**
     * Constructs a ModelResource with the specified Descriptor.
     * @param desc the Descriptor
     */
    public ModelResource(Descriptor desc) {
        _desc = desc != null ? desc : new Descriptor();
    }

    /**
     * Gets the Descriptor used by this ModelResource.
     * @return the Descriptor
     */
    public final Descriptor getDescriptor() {
        return _desc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public M pull(InputStream is) throws IOException {
        return pull(new ElementResource().pull(is));
    }

    /**
     * Safely pulls a Model from a Reader.
     * @param reader a Reader of the Model
     * @return the Model, or null if not found
     * @throws IOException if a problem occurred
     */
    public M pull(Reader reader) throws IOException {
        return pull(new ElementResource().pull(reader));
    }

    /**
     * Safely pulls a Model from an InputSource.
     * @param is an InputSource of the Model
     * @return the Model, or null if not found
     * @throws IOException if a problem occurred
     */
    public M pull(InputSource is) throws IOException {
        return pull(new ElementResource().pull(is));
    }

    /**
     * Safely constructs a Model from a Document.
     * @param document the Model Document
     * @return the Model, or null if document is null
     */
    public M pull(Document document) {
        return pull(new ElementResource().pull(document));
    }

    /**
     * Safely constructs a Model from an Element.
     * @param element the Model element
     * @return the Model, or null if element is null
     */
    @SuppressWarnings("unchecked")
    public M pull(Element element) {
        String namespace = element.getNamespaceURI();
        if (namespace != null) {
            Marshaller marshaller = _desc.getMarshaller(namespace);
            if (marshaller != null) {
                return (M)marshaller.read(new ConfigurationResource().pull(element));
            }
        }
        return null;
    }

    /**
     * Safely pulls (constructs) a basic Model from a qualified name.
     * @param qname the qualified name
     * @return the model, or null if the qualified name is null
     */
    public M pull(QName qname) {
        return pull(new ElementResource().pull(qname));
    }

}
