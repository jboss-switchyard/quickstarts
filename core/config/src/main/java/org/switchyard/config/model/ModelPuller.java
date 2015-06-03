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
package org.switchyard.config.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.namespace.QName;

import org.switchyard.common.io.pull.ElementPuller;
import org.switchyard.common.io.pull.Puller;
import org.switchyard.config.ConfigurationPuller;
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
public class ModelPuller<M extends Model> extends Puller<M> {

    private Descriptor _desc;

    /**
     * Constructs a default ModelPuller (ignoring comments when parsing XML) with a default Descriptor.
     */
    public ModelPuller() {
        this(null);
    }

    /**
     * Constructs a ModelPuller (ignoring comments when parsing XML) with the specified Descriptor.
     * @param desc the Descriptor
     */
    public ModelPuller(Descriptor desc) {
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
    public M pull(InputStream stream) throws IOException {
        return pull(new ElementPuller().pull(stream));
    }

    /**
     * Safely pulls a Model from a Reader.
     * @param reader a Reader of the Model
     * @return the Model, or null if not found
     * @throws IOException if a problem occurred
     */
    public M pull(Reader reader) throws IOException {
        return pull(new ElementPuller().pull(reader));
    }

    /**
     * Safely pulls a Model from an InputSource.
     * @param source an InputSource of the Model
     * @return the Model, or null if not found
     * @throws IOException if a problem occurred
     */
    public M pull(InputSource source) throws IOException {
        return pull(new ElementPuller().pull(source));
    }

    /**
     * Safely constructs a Model from a Document.
     * @param document the Model Document
     * @return the Model, or null if document is null
     */
    public M pull(Document document) {
        return pull(new ElementPuller().pull(document));
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
                return (M) marshaller.read(new ConfigurationPuller().pull(element));
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
        return pull(new ElementPuller().pull(qname));
    }

}
