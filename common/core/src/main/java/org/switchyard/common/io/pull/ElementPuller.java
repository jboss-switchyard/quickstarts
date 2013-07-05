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

package org.switchyard.common.io.pull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class to safely access ("pull") DOM elements from various sources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ElementPuller extends Puller<Element> {

    private final boolean _ignoringComments;

    /**
     * Constructs a new ElementPuller (ignoring comments when parsing XML).
     */
    public ElementPuller() {
        _ignoringComments = true;
    }

    /**
     * Constructs a new ElementPuller (optionally ignoring comments when parsing XML).
     * @param ignoringComments whether comments should be ignored when parsing XML.
     */
    public ElementPuller(boolean ignoringComments) {
        _ignoringComments = ignoringComments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element pull(InputStream stream) throws IOException {
        return pull(new InputSource(stream));
    }

    /**
     * Pulls an Element from a Reader.
     * @param reader a Reader of the element
     * @return the element
     * @throws IOException if a problem occurred
     */
    public Element pull(Reader reader) throws IOException {
        return pull(new InputSource(reader));
    }

    /**
     * Pulls an Element from an InputSource.
     * @param source an InputSource of the element
     * @return the element
     * @throws IOException if a problem occurred
     */
    public Element pull(InputSource source) throws IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(_ignoringComments);
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        try {
            return pull(factory.newDocumentBuilder().parse(source));
        } catch (ParserConfigurationException pce) {
            throw new IOException(pce);
        } catch (SAXException se) {
            throw new IOException(se);
        }
    }

    /**
     * Pulls the root element of a DOM document, additionally normalizing it.
     * @param document the document
     * @return the element
     */
    public Element pull(Document document) {
        return pull(document, true);
    }

    /**
     * Pulls the root element of a DOM document.
     * @param document the document
     * @param normalize whether or not to normalize the document
     * @return the element
     */
    public Element pull(Document document, boolean normalize) {
        if (normalize) {
            document.normalizeDocument();
        }
        return pull(document.getDocumentElement(), normalize);
    }

    /**
     * Returns the element itself, additionally normalizing it.
     * @param element the element
     * @return the element
     */
    public Element pull(Element element) {
        return pull(element, true);
    }

    /**
     * Returns the element, optionally normalizing it.
     * @param element the element
     * @param normalize whether or not to normalize the element
     * @return the element
     */
    public Element pull(Element element, boolean normalize) {
        if (normalize) {
            element.normalize();
        }
        return element;
    }

    /**
     * Pulls (constructs) a basic Element from a qualified name.
     * @param qname the qualified name
     * @return the element
     */
    public Element pull(QName qname) {
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        String prefix = qname.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            sb.append(prefix);
            sb.append(':');
        }
        sb.append(qname.getLocalPart());
        String namespace = qname.getNamespaceURI();
        if (namespace != null && namespace.length() > 0) {
            sb.append(" xmlns");
            if (prefix != null && prefix.length() > 0) {
                sb.append(':');
                sb.append(prefix);
            }
            sb.append("=\"");
            sb.append(namespace);
            sb.append('"');
        }
        sb.append("/>");
        try {
            return pull(new StringReader(sb.toString()));
        } catch (IOException ioe) {
            // shouldn't happen
            throw new RuntimeException(ioe);
        }
    }

}
